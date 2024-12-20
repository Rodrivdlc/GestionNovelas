## Gestion Novelas

https://github.com/Rodrivdlc/GestionNovelas.git

# Ultima actualización:
He añadido una opcion nueva para añadir la ubicación de la novela, la ubicación se guarda en firebase y en la lista de novelas aparece un boton para ver el mapa.


# Actualización anterior: Feedback 5:
Después de implementar las mejoras indicadas en el enunciado, realicé pruebas y observé mejoras significativas en la aplicación:

Optimización del Uso de la Memoria:

Reducción de fugas de memoria: Utilicé el Memory Profiler para identificar y corregir problemas de gestión de memoria. Aseguré que los cursores y conexiones a la base de datos se cerraran adecuadamente en la clase DatabaseHelper, evitando fugas de memoria.
Uso eficiente de recursos: Aunque no se manejaban imágenes grandes, revisé el código para garantizar un manejo eficiente de las vistas y estados en Jetpack Compose, evitando recomposiciones innecesarias y optimizando el uso de la memoria.
Mejora del Rendimiento de la Red:

Análisis y optimización de solicitudes de red: Con el Network Profiler, analicé las interacciones con Firebase y optimicé el uso de la red. Implementé la persistencia local de Firebase con setPersistenceEnabled(true), lo que redujo las solicitudes de red al aprovechar el caché local.
Reducción del consumo de datos: Limité la carga inicial de datos usando limitToLast(50), disminuyendo el volumen de datos descargados y mejorando los tiempos de respuesta.
Optimización del Uso de la Batería:

Identificación de procesos que consumen batería: Utilicé batterystats y Battery Historian para detectar actividades en segundo plano que consumían batería, como listeners activos constantemente.
Implementación de prácticas recomendadas: Reemplacé los listeners activos por sincronizaciones periódicas usando WorkManager, programando tareas cada hora. Esto redujo la actividad en segundo plano y, por ende, el consumo de batería.
Tras las pruebas realizadas con estas mejoras, noté que:

La aplicación es más eficiente en el uso de memoria, evitando cierres inesperados y mejorando la experiencia del usuario.
El consumo de datos móviles se redujo, lo que beneficia a usuarios con planes de datos limitados.
La duración de la batería mejoró, ya que la aplicación minimiza las actividades en segundo plano innecesarias.

## Descripción
Biblioteca de Novelas es una aplicación de Android para gestionar una colección de novelas. Los usuarios pueden registrarse, iniciar sesión y mantener un registro de sus novelas favoritas, incluyendo información detallada como el título, autor, año de publicación y sinopsis. Cada novela puede ser marcada como favorita y recibir reseñas de los usuarios. La app utiliza Firebase Realtime Database para almacenar la información de las novelas, SQLite para gestionar las credenciales de usuario, y SharedPreferences para guardar las preferencias de personalización, como el color de fondo en la pantalla de inicio de sesión.

## Tecnologías Usadas

## 🔥 Firebase Realtime Database 

Almacena y sincroniza en tiempo real los datos de las novelas.
Permite que cada novela se guarde con sus detalles (título, autor, año de publicación, sinopsis, estado de favorito y reseñas) y actualiza los cambios instantáneamente para todos los usuarios conectados.

## 📂 SQLite

Gestiona la autenticación de usuarios.
Permite registrar e iniciar sesión de usuarios con nombre y contraseña, almacenando estos datos en una base de datos local en el dispositivo.

## ⚙️ SharedPreferences

Almacena la preferencia del color de fondo de la pantalla de inicio de sesión, permitiendo a los usuarios personalizar la interfaz de la aplicación y guardar estas preferencias de manera persistente.
Funcionalidades
Registro e Inicio de Sesión: Los usuarios pueden crear una cuenta con su nombre y contraseña, almacenados en SQLite, y acceder a su biblioteca de novelas.
Agregar Novelas: Permite agregar nuevas novelas a la biblioteca proporcionando título, autor, año de publicación y sinopsis.
Ver y Editar Detalles de Novelas: Los usuarios pueden ver detalles completos de cada novela, añadir o modificar sus reseñas y cambiar el estado de favorito.
Favoritos: Permite marcar novelas como favoritas para identificarlas fácilmente en la biblioteca.
Cambio de Color de Fondo en Pantalla de Login: Los usuarios pueden cambiar el color de fondo de la pantalla de inicio de sesión y la preferencia se guarda usando SharedPreferences.

## Manual de uso de la aplicación "Biblioteca de Novelas"

Descripción de la aplicación
La aplicación "Biblioteca de Novelas" permite gestionar una lista personal de novelas, permitiendo al usuario agregar nuevas novelas, eliminarlas, marcarlas como favoritas y ver detalles adicionales, incluidas reseñas. La interfaz es sencilla y fácil de usar.

Pantalla principal
Al abrir la aplicación, encontrarás una serie de campos que te permiten añadir nuevas novelas a tu biblioteca. A continuación, una lista de las novelas que has agregado, con opciones para interactuar con cada una de ellas.

Funciones principales
1. Agregar una novela
   
Para agregar una nueva novela a tu biblioteca:

Completa los siguientes campos en la parte superior de la pantalla:
Título: Introduce el título de la novela.
Autor: Introduce el nombre del autor o autora.
Año de Publicación: Introduce el año en que fue publicada la novela (debe ser un número).
Sinopsis: Escribe una breve descripción o sinopsis de la novela.
Pulsa el botón Añadir Novela para agregarla a la lista.

2. Lista de novelas

Cada novela añadida mostrará su título, autor, año de publicación y si está marcada como favorita. Además, cada novela tiene dos botones:

Eliminar: Para eliminar la novela de la lista.
Marcar como Favorita: Para marcar la novela como favorita (o quitarla de favoritos si ya lo está).

3. Ver detalles de una novela
   
Para ver más detalles de una novela, haz clic sobre cualquiera de ellas en la lista.
Se abrirá una ventana emergente con los siguientes detalles:
Autor: Nombre del autor.
Año de publicación: Año en que fue publicada.
Sinopsis: La descripción breve de la novela.
Favorita: Si está marcada como favorita o no.
Además, podrás ver las reseñas que hayan sido añadidas a la novela.

4. Añadir una reseña
   
En la ventana emergente de detalles de la novela, encontrarás un campo de texto para Agregar una reseña.
Escribe tu reseña en este campo y pulsa el botón Aceptar.
Tu reseña será guardada y aparecerá listada debajo del campo de entrada.

5. Cerrar la ventana de detalles
   
Una vez que hayas revisado los detalles de la novela o añadido una reseña, puedes cerrar la ventana emergente pulsando el botón Cerrar.
