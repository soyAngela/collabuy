<?php

include 'conexion_collabuy.php';

$usuario = $_POST["usuario"];
$contrasena = $_POST["contrasena"];

$resultado = mysqli_query($con, "SELECT
                                     CASE WHEN EXISTS
                                         (SELECT  usuario
                                          FROM Usuario
                                          WHERE usuario='$usuario' and contrasena='$contrasena')
                                              THEN 1
                                          ELSE 0
                                         END
                                 FROM Usuario");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

$fila = mysqli_fetch_row($resultado);

if($fila[0] == 1){
    echo '1';
}else{
    echo '0';
}

mysqli_close($con);
?>