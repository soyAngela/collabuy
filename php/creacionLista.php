<?php
include 'conexion_collabuy.php';

$usuario = $_POST["usuario"];
$nombreLista = $_POST["nombre"];
$clave = $_POST["clave"];

$resultado = mysqli_query($con, "SELECT CASE WHEN EXISTS (SELECT * FROM Lista WHERE nombre = '$nombreLista' AND clave = '$clave') THEN 1 ELSE 0 END FROM Lista");

$fila = mysqli_fetch_row($resultado);
if($fila[0] == 0){
    $resultado1 = mysqli_query ($con, "INSERT INTO Lista(nombre, clave) VALUES ('$nombreLista','$clave')");
    if ($resultado1 == true){
        $resultado2 = mysqli_query($con , "SELECT id FROM Lista WHERE nombre = '$nombreLista' AND clave = '$clave'");
        $data = array();
        #Construir la lista elemento a elemento
        while ($fila = mysqli_fetch_assoc($resultado2)) {
            $data = ['id' => $fila['id']];
        }
        $id = $data['id'];
        $resultado3 = mysqli_query($con, "INSERT INTO Participacion (id_lista, id_usuario) VALUES ('$id', '$usuario')");
        if ($resultado3 == true){
            echo "lista creada";
        }
    }
    else{
        echo "error de creacion";
    }
}else{
    echo "lista existente";
}

