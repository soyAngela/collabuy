<?php
include 'comprobarLista.php';

$data = json_decode($res, true);
$usu = $data['usuario'];
$id = $data['id'];
$nomLista = $data['nombre'];
$claveLista = $data['clave'];

$resultado = mysqli_query($con, "SELECT * FROM Participacion WHERE id_lista = '$id' AND id_usuario = '$usu'");


if (!$resultado){
    $resultado1 = mysqli_query($con, "INSERT INTO Participacion (id_lista, id_usuario) VALUES ('$id', '$usu')");
    if ($resultado1){
        echo "lista añadida";
    }
    else{
        echo "no se ha introducido";
    }
}
else{
    echo "ya participaba";
}
?>