# app.py
from flask import Flask
from flask_jwt_extended import JWTManager
from db import db
import models
from users import users_bp
from toilets import toilets_bp
from reviews import reviews_bp
from favorites import fav_bp

def create_app():
    app = Flask(__name__)
    app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:negros@127.0.0.1:3306/dbbñaos'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.config['JWT_SECRET_KEY'] = 'cambia_esto_por_una_clave_muy_larga_y_segura'

    db.init_app(app)
    jwt = JWTManager(app)      # inicializa JWT

    with app.app_context():
        db.create_all()

    app.register_blueprint(users_bp)
    app.register_blueprint(toilets_bp)
    app.register_blueprint(reviews_bp)
    app.register_blueprint(fav_bp)       # ← registramos favoritos


    return app

if __name__ == '__main__':
    # host="0.0.0.0" hace que acepte conexiones externas
    create_app().run(host="0.0.0.0", port=5000)
    
