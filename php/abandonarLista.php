<?php

$id = $_POST["id"];
$usuario = $_POST["usuario"];

include 'conexion_collabuy.php';

$resultado = mysqli_query($con, "DELETE FROM Participacion WHERE id_lista = '$id' AND id_usuario = '$usuario'");

if ($resultado == true){
    $consulta = mysqli_query($con, "SELECT COUNT(*) FROM Participacion WHERE id_lista = '$id'");
    $fila = mysqli_fetch_row($consulta);
    if($fila[0] == 0){
        $eliminar = mysqli_query($con, "DELETE FROM Lista WHERE id = '$id'");
        if ($eliminar == true){
            echo "lista eliminada";
        }else{
            echo "no se ha eliminado";
        }
    }else{
        echo "quedan participantes";
    }
}

mysqli_close($con);
?>