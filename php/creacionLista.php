<?php
include 'conexion_collabuy.php';

$usuario = $_POST["usuario"];
$nombreLista = $_POST["nombre"];
$clave = $_POST["clave"];

$resultado = mysqli_query($con, "SELECT * FROM Lista WHERE nombre = '$nombreLista' AND clave = '$clave'");

if (!$resultado) {
    $resultado1 = mysqli_query ($con, "INSERT INTO Lista(nombre, clave) VALUES ('$nombre','$clave')");
    if ($resultado1 == true){
        $resultado2 = mysqli_query($con , "SELECT id FROM Lista WHERE nombre = '$nombreLista' AND clave = '$clave'");
        $arrayresultados = array();
        #Construir la lista elemento a elemento
        while ($fila = mysqli_fetch_assoc($resultado2)) {
            $arrayresultados = ['id' => $fila['id']];
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

