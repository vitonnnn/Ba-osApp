# en user_contrib.py (nuevo fichero)
from flask import Blueprint, jsonify, abort
from flask_jwt_extended import jwt_required, get_jwt_identity
from db import db
from models import Toilet, Review, User

users_bp = Blueprint('users', __name__, url_prefix='/users')

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