<?php

include 'conexion_collabuy.php';

$id = $_POST["id"];
$nombre = $_POST["nombre"];
$clave = $_POST["clave"];

$resultado = mysqli_query($con, "SELECT CASE WHEN EXISTS (SELECT * FROM Lista WHERE nombre = '$nombre' AND clave = '$clave') THEN 1 ELSE 0 END FROM Lista");
$fila = mysqli_fetch_row($resultado);
if ($fila[0] == 0){
    $edicion = mysqli_query($con,"UPDATE Lista SET nombre = '$nombre', clave = '$clave' WHERE id = '$id'");
    if ($edicion == true){
        echo "lista actualizada";
    }
    else{
        echo "no actualizada";
    }
}else{
    echo "ya existe";
}

mysqli_close($con);

?>