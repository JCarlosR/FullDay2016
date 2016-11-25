Cada vez que un usuario decida subir una imagen a la galer�a, entonces hemos de crear un nuevo objeto en el nodo "images" (firebase database) y guardar la imagen en una carpeta "images" (firebase storage) adem�s de su correspondiente miniatura en "thumbnails".

De esta manera el nodo images en la base de datos almacenar� objetos con la informaci�n del usuario que ha publicado, mientras que la imagen a tama�o completo y su miniatura ser�n registradas en el storage.

Ejemplo (Firebase Database):
images: {
	imageXYZ: {
		id: 5,
		name: "Juan Ramos"
	},
	imageABC: {
		id: 7,
		name: "Mar�a"
	}
}

Ejemplo (Firebase Storage):
images: 
	imageXYZ.jpg
	imageABC.jpg
thumbnails:
	imageXYZ.jpg
	imageABC.jpg