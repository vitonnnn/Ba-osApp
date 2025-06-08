# users.py
from flask import Blueprint, request, jsonify, abort
from db import db
from models import Toilet, Review, User
from flask_jwt_extended import jwt_required, get_jwt_identity
from werkzeug.security import generate_password_hash, check_password_hash
from flask_jwt_extended import create_access_token

users_bp = Blueprint('users', __name__, url_prefix='/users')

@users_bp.route('', methods=['POST'])
def create_user():
    data = request.json or {}
    u = data.get('username')
    p = data.get('password')
    e = data.get('email', '')
    if not u or not p:
        return jsonify({'message':'username y password obligatorios'}), 400
    if User.query.filter_by(username=u).first():
        return jsonify({'message':'El usuario ya existe'}), 409

    hashed = generate_password_hash(p, method='pbkdf2:sha256')
    user = User(username=u, password=hashed, email=e)
    db.session.add(user)
    db.session.commit()
    return jsonify({
        'id': user.id,
        'username': user.username,
        'email': user.email
    }), 201

@users_bp.route('/login', methods=['POST'])
def login():
    data = request.json or {}
    u = data.get('username')
    p = data.get('password')
    if not u or not p:
        return jsonify({'message':'username y password requeridos'}), 400

    user = User.query.filter_by(username=u).first()
    if not user or not check_password_hash(user.password, p):
        return jsonify({'message':'Credenciales inválidas'}), 401

    # Emitimos identity como string para que el claim "sub" sea una cadena
    access_token = create_access_token(identity=str(user.id))
    return jsonify({
        'access_token': access_token,
        'user': {
            'id': user.id,
            'username': user.username,
            'email': user.email
        }
    }), 200

@users_bp.route('/<int:user_id>', methods=['GET'])
def get_user(user_id):
    user = User.query.get_or_404(user_id)
    return jsonify({
        'id': user.id,
        'username': user.username
    }), 200
@users_bp.route('/<int:u_id>/contributions', methods=['GET'])
@jwt_required()
def list_contributions(u_id):
    current_id = int(get_jwt_identity())
    current_user = User.query.get_or_404(current_id)

    # Si no es el propio usuario ni admin1, prohibido
    if current_id != u_id and current_user.username != "admin1":
        abort(403)

    # Si es admin1, devolvemos TODO; en otro caso, solo lo suyo
    if current_user.username == "admin1":
        toilets = Toilet.query.all()
        revs = db.session.query(Review, Toilet).join(Toilet).all()
    else:
        toilets = Toilet.query.filter_by(user_id=u_id).all()
        revs = (
            db.session.query(Review, Toilet)
            .join(Toilet)
            .filter(Review.user_id == u_id)
            .all()
        )

    toilets_data = [{
        'id': t.id, 'name': t.name,
        'latitude': float(t.latitude), 'longitude': float(t.longitude),
        'avg_rating': float(t.avg_rating),
        'accesible': t.accesible, 'publico': t.publico,
        'mixto': t.mixto, 'cambio_bebes': t.cambio_bebes
    } for t in toilets]

    reviews_data = [{
        'id': r.id,
        'toiletId': r.toilet_id,
        'toiletName': toilet.name,
        'valoracion': r.valoracion,
        'limpieza': r.limpieza,
        'olor': r.olor,
        'comment': r.comment,
        'created_at': r.created_at.isoformat()
    } for r, toilet in revs]

    return jsonify({
        'toilets': toilets_data,
        'reviews': reviews_data
    }), 200
