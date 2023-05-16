<?php

$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xagonzalez488"; #el usuario para esa base de datos
$DB_PASS="hi0uYGd9"; #la clave para ese usuario
$DB_DATABASE="Xagonzalez488_Collabuy"; #la base de datos a la que hay que conectarse

# Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobamos conexión
if (mysqli_connect_errno()) {
    echo 'Error de conexion: ' . mysqli_connect_error();
    exit();
}

?>