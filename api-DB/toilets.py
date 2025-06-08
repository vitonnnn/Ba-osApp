# toilets.py
from flask import Blueprint, request, jsonify, abort
from db import db
from flask_jwt_extended import jwt_required, get_jwt_identity 
from models import Toilet, Review, UserFavorite, User
from sqlalchemy import func, desc, asc

toilets_bp = Blueprint('toilets', __name__, url_prefix='/toilets')


@toilets_bp.route('', methods=['GET'])
def list_toilets():
    """
    - Si no se pasan parámetros, devuelve todos los baños.
    """
    all_toilets = Toilet.query.all()
    result = []
    for t in all_toilets:
        result.append({
            'id': t.id,
            'name': t.name,
            'latitude': float(t.latitude),
            'longitude': float(t.longitude),
            'avg_rating': float(t.avg_rating),
            'accesible': t.accesible,
            'publico': t.publico,
            'mixto': t.mixto,
            'cambio_bebes': t.cambio_bebes
        })
    return jsonify(result)


@toilets_bp.route('/nearby', methods=['GET'])
def list_nearby_toilets():
    """
    Devuelve solo los baños pertenecientes al radio 'radius' (5–100 km)
    alrededor de (lat, lon), y aplica filtros booleanos si se proporcionan:
      - mixto=true|false
      - accesible=true|false
      - publico=true|false
      - cambio_bebes=true|false
    Además acepta sort=asc|desc (por avg_rating).
    """
    lat_str = request.args.get('lat')
    lon_str = request.args.get('lon')
    radius_str = request.args.get('radius')

    if lat_str is None or lon_str is None or radius_str is None:
        return jsonify({'error': 'Debe especificar los parámetros lat, lon y radius'}), 400

    try:
        lat = float(lat_str)
        lon = float(lon_str)
        radius = float(radius_str)
    except ValueError:
        return jsonify({'error': 'lat, lon y radius deben ser números válidos'}), 400

    if radius < 5 or radius > 100:
        return jsonify({'error': 'radius debe estar entre 5 y 100 km'}), 400

    # Leemos directamente los parámetros booleanos como strings
    mixto_param = request.args.get('mixto')
    accesible_param = request.args.get('accesible')
    publico_param = request.args.get('publico')
    cambio_bebes_param = request.args.get('cambio_bebes')
    sort_order = request.args.get('sort', 'desc').lower()

    # Convertimos solo si vienen explícitamente "true" o "false"
    def str_to_bool(s: str):
        return s.lower() == 'true'

    # Preparamos la expresión de distancia (Haversine)
    distance_expr = 6371 * func.acos(
        func.cos(func.radians(lat)) *
        func.cos(func.radians(Toilet.latitude)) *
        func.cos(func.radians(Toilet.longitude) - func.radians(lon)) +
        func.sin(func.radians(lat)) *
        func.sin(func.radians(Toilet.latitude))
    )

    # Orden por avg_rating
    order_clause = asc(Toilet.avg_rating) if sort_order == 'asc' else desc(Toilet.avg_rating)

    # Construimos la query base: distancia <= radius
    query = db.session.query(Toilet, distance_expr.label('distance')).filter(distance_expr <= radius)

    # Aplicamos filtros booleanos únicamente si se proporciona el parámetro
    if mixto_param is not None:
        # si mixto_param == "true", filtramos Toilet.mixto == True
        # si mixto_param == "false", filtramos Toilet.mixto == False
        try:
            mixto_bool = str_to_bool(mixto_param)
        except:
            return jsonify({'error': 'mixto debe ser "true" o "false"'}), 400
        query = query.filter(Toilet.mixto == mixto_bool)

    if accesible_param is not None:
        try:
            accesible_bool = str_to_bool(accesible_param)
        except:
            return jsonify({'error': 'accesible debe ser "true" o "false"'}), 400
        query = query.filter(Toilet.accesible == accesible_bool)

    if publico_param is not None:
        try:
            publico_bool = str_to_bool(publico_param)
        except:
            return jsonify({'error': 'publico debe ser "true" o "false"'}), 400
        query = query.filter(Toilet.publico == publico_bool)

    if cambio_bebes_param is not None:
        try:
            cambio_bebes_bool = str_to_bool(cambio_bebes_param)
        except:
            return jsonify({'error': 'cambio_bebes debe ser "true" o "false"'}), 400
        query = query.filter(Toilet.cambio_bebes == cambio_bebes_bool)

    # Finalmente aplicamos orden y obtenemos resultados
    query = query.order_by(order_clause)
    toilets_and_dist = query.all()

    result = []
    for toilet, dist in toilets_and_dist:
        result.append({
            'id': toilet.id,
            'name': toilet.name,
            'latitude': float(toilet.latitude),
            'longitude': float(toilet.longitude),
            'avg_rating': float(toilet.avg_rating),
            'accesible': toilet.accesible,
            'publico': toilet.publico,
            'mixto': toilet.mixto,
            'cambio_bebes': toilet.cambio_bebes,
            'distance_km': round(dist, 2)
        })

    return jsonify(result)
@toilets_bp.route('', methods=['POST'])
@jwt_required()
def create_toilet():
    user_id = int(get_jwt_identity())  # ⬅ obtener usuario autenticado
    data = request.get_json() or {}
    name = data.get('name')
    latitude = data.get('latitude')
    longitude = data.get('longitude')
    if not name or latitude is None or longitude is None:
        return jsonify({'error': 'name, latitude y longitude son obligatorios'}), 400

    new_toilet = Toilet(
        name         = name,
        latitude     = latitude,
        longitude    = longitude,
        avg_rating   = 0.0,
        accesible    = data.get('accesible', False),
        publico      = data.get('publico', False),
        mixto        = data.get('mixto', False),
        cambio_bebes = data.get('cambio_bebes', False),
        user_id      = user_id  # ⬅ guardar usuario
    )

    db.session.add(new_toilet)
    db.session.commit()

    return jsonify({
        'id': new_toilet.id,
        'name': new_toilet.name,
        'latitude': float(new_toilet.latitude),
        'longitude': float(new_toilet.longitude),
        'avg_rating': float(new_toilet.avg_rating),
        'accesible': new_toilet.accesible,
        'publico': new_toilet.publico,
        'mixto': new_toilet.mixto,
        'cambio_bebes': new_toilet.cambio_bebes,
        'user_id': new_toilet.user_id  # opcional
    }), 201

@toilets_bp.route('/<int:id>', methods=['DELETE'])
@jwt_required()
def delete_toilet(id):
    toilet = Toilet.query.get_or_404(id)
    current_id = int(get_jwt_identity())
    current_user = User.query.get_or_404(current_id)

    # Solo el creador o admin1 puede borrar
    if toilet.user_id != current_id and current_user.username != "admin1":
        abort(403)

    # Primero, retirar favoritos (previene IntegrityError)
    UserFavorite.query.filter_by(toilet_id=id).delete()

    db.session.delete(toilet)
    db.session.commit()
    return jsonify({'message': 'Toilet eliminado'}), 200