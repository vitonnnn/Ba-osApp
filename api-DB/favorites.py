# favorites.py
from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from db import db
from models import UserFavorite, Toilet

fav_bp = Blueprint('favorites', __name__, url_prefix='/favorites')

@fav_bp.route('', methods=['GET'])
@jwt_required()
def list_favorites():
    # get_jwt_identity() devuelve string; lo convertimos a entero
    user_id = int(get_jwt_identity())
    favs = UserFavorite.query.filter_by(user_id=user_id).all()
    result = []
    for uf in favs:
        t = Toilet.query.get(uf.toilet_id)
        if t:
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
    return jsonify(result), 200

@fav_bp.route('', methods=['POST'])
@jwt_required()
def add_favorite():
    user_id = int(get_jwt_identity())
    data = request.json or {}
    # Aceptamos tanto snake_case como camelCase
    toilet_id = data.get('toilet_id') or data.get('toiletId')
    if not toilet_id:
        return jsonify({'message':'toilet_id requerido'}), 400

    if not Toilet.query.get(toilet_id):
        return jsonify({'message':'Baño no existe'}), 404

    if UserFavorite.query.get((user_id, toilet_id)):
        return jsonify({'message':'Ya es favorito'}), 409

    fav = UserFavorite(user_id=user_id, toilet_id=toilet_id)
    db.session.add(fav)
    db.session.commit()
    return jsonify({'message':'Favorito añadido'}), 201

@fav_bp.route('/<int:toilet_id>', methods=['DELETE'])
@jwt_required()
def remove_favorite(toilet_id):
    user_id = int(get_jwt_identity())
    fav = UserFavorite.query.get((user_id, toilet_id))
    if not fav:
        return jsonify({'message':'No estaba en favoritos'}), 404
    db.session.delete(fav)
    db.session.commit()
    return jsonify({'message':'Favorito eliminado'}), 200
