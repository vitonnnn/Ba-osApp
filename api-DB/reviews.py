# reviews.py
from flask import Blueprint, request, jsonify, abort
from db import db
from models import Toilet, Review, User
from flask_jwt_extended import jwt_required, get_jwt_identity

reviews_bp = Blueprint('reviews', __name__, url_prefix='/toilets/<int:t_id>/reviews')


@reviews_bp.route('', methods=['GET'])
def list_reviews(t_id):
    # Asegura que el baño existe
    Toilet.query.get_or_404(t_id)

    # Recupera reseñas junto con el username del autor
    revs = (
        db.session.query(Review, User)
        .join(User, Review.user_id == User.id)
        .filter(Review.toilet_id == t_id)
        .all()
    )

    return jsonify([
        {
            'id': r.id,
            'user_id': r.user_id,
            'username': user.username,
            'valoracion': r.valoracion,
            'limpieza': r.limpieza,
            'olor': r.olor,
            'comment': r.comment,
            'created_at': r.created_at.isoformat()
        }
        for r, user in revs
    ])


@reviews_bp.route('', methods=['POST'])
@jwt_required()
def add_review(t_id):
    user_id = int(get_jwt_identity())
    # Asegura que el baño existe
    toilet = Toilet.query.get_or_404(t_id)

    data = request.get_json() or {}
    review = Review(
        toilet_id  = toilet.id,
        user_id    = user_id,
        valoracion = data.get('valoracion', 0),
        limpieza   = data.get('limpieza', 0),
        olor       = data.get('olor', 0),
        comment    = data.get('comment', '')
    )

    db.session.add(review)
    db.session.flush()  # para que toilet.reviews incluya la nueva reseña

    # Recalcula avg_rating con todas las reseñas
    ratings = [r.valoracion for r in toilet.reviews]
    toilet.avg_rating = round(sum(ratings) / len(ratings), 1)

    db.session.commit()
    return jsonify({'message': 'Review creada', 'id': review.id}), 201

# en reviews.py :contentReference[oaicite:1]{index=1}
@reviews_bp.route('/<int:r_id>', methods=['DELETE'])
@jwt_required()
def delete_review(t_id, r_id):
    review = Review.query.get_or_404(r_id)
    current_id = int(get_jwt_identity())
    current_user = User.query.get_or_404(current_id)

    # Solo el autor o admin1 puede borrar
    if review.user_id != current_id and current_user.username != "admin1":
        abort(403)

    db.session.delete(review)
    db.session.commit()
    return jsonify({'message': 'Review eliminada'}), 200

