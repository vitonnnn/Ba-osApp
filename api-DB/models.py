# models.py
from datetime import datetime
from db import db

class User(db.Model):
    __tablename__ = 'users'
    id         = db.Column(db.BigInteger, primary_key=True)
    username   = db.Column(db.String(50), unique=True, nullable=False)
    password   = db.Column(db.String(255), nullable=False)
    email      = db.Column(db.String(100), unique=True, nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)
    reviews    = db.relationship('Review', backref='author', cascade='all, delete')

class Toilet(db.Model):
    __tablename__ = 'toilets'
    id           = db.Column(db.BigInteger, primary_key=True)
    name         = db.Column(db.String(100), nullable=False)
    latitude     = db.Column(db.Numeric(9,6), nullable=False)
    longitude    = db.Column(db.Numeric(9,6), nullable=False)
    avg_rating   = db.Column(db.Numeric(2,1), default=0.0, nullable=False)
    accesible    = db.Column(db.Boolean, default=False, nullable=False)
    publico      = db.Column(db.Boolean, default=False, nullable=False)
    mixto        = db.Column(db.Boolean, default=False, nullable=False)
    cambio_bebes = db.Column(db.Boolean, default=False, nullable=False)

    user_id      = db.Column(db.BigInteger, db.ForeignKey('users.id'), nullable=False)

    # ← Aquí definimos la relación hacia Review:
    reviews      = db.relationship(
        'Review',
        backref='toilet',
        cascade='all, delete-orphan',
        lazy='dynamic'
    )

class Review(db.Model):
    __tablename__ = 'reviews'
    id         = db.Column(db.BigInteger, primary_key=True)
    toilet_id  = db.Column(db.BigInteger, db.ForeignKey('toilets.id'), nullable=False)
    user_id    = db.Column(db.BigInteger, db.ForeignKey('users.id'), nullable=False)
    valoracion = db.Column(db.SmallInteger, nullable=False)
    limpieza   = db.Column(db.SmallInteger, nullable=False)
    olor       = db.Column(db.SmallInteger, nullable=False)
    comment    = db.Column(db.Text)
    created_at = db.Column(db.DateTime, default=db.func.now())
class UserFavorite(db.Model):
    __tablename__ = 'user_favorites'
    user_id   = db.Column(db.BigInteger, db.ForeignKey('users.id'), primary_key=True)
    toilet_id = db.Column(db.BigInteger, db.ForeignKey('toilets.id'), primary_key=True)
    added_at  = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)

# Para facilitar accesos inversos, opcionalmente añade en User y Toilet:
# User.favorites = db.relationship('UserFavorite', backref='user', cascade='all, delete')
# Toilet.favorited_by = db.relationship('UserFavorite', backref='toilet', cascade='all, delete')
