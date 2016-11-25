Cada vez que un usuario decida subir una imagen a la galería, entonces hemos de crear un nuevo objeto en el nodo "images" (firebase database) y guardar la imagen en una carpeta "images" (firebase storage) además de su correspondiente miniatura en "thumbnails".

De esta manera el nodo images en la base de datos almacenará objetos con la información del usuario que ha publicado, mientras que la imagen a tamaño completo y su miniatura serán registradas en el storage.

Ejemplo (Firebase Database):
images: {
	imageXYZ: {
		id: 5,
		name: "Juan Ramos"
	},
	imageABC: {
		id: 7,
		name: "María"
	}
}

Ejemplo (Firebase Storage):
images: 
	imageXYZ.jpg
	imageABC.jpg
thumbnails:
	imageXYZ.jpg
	imageABC.jpg