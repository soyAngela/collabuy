<?php
include 'conexion_collabuy.php';

$usuario = $_POST["usuario"];

# Ejecutar la sentencia SQL
$resultado = mysqli_query($con, "SELECT * FROM Lista INNER JOIN Participacion ON Lista.id = Participacion.id_lista WHERE id_usuario = '$usuario'");

# Comprobar si se ha ejecutado correctamente
if (!$resultado) {
    echo 'Ha ocurrido algun error: ' . mysqli_error($con);
}

while ($fila = mysqli_fetch_assoc($resultado)) {
    # Generar el array con los resultados con la forma Atributo - Valor
    $arrayresultados[] = array('id' => $fila['id'], 'nombre' => $fila['nombre'], 'clave' => $fila['clave']);
}

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);
echo $res;

mysqli_close($con);
?>
