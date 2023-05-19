<?php
include 'conexion_collabuy.php';

$parametro = $_POST["usuario"];

# Ejecutar la sentencia SQL
$resultado = mysqli_query($con, "SELECT * FROM Lista INNER JOIN Participacion ON Lista.id = Participacion.id_lista WHERE id_usuario = 'lucas'");

# Comprobar si se ha ejecutado correctamente
if (!$resultado) {
    echo 'Ha ocurrido algun error: ' . mysqli_error($con);
}

#Acceder al resultado
$fila = mysqli_fetch_row($resultado);

# Generar el array con los resultados con la forma Atributo - Valor
$arrayresultados = array('id' => $fila[0], 'nombre' => $fila[1], 'clave' => $fila[2]);

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);
echo $res;

mysqli_close($con);
?>
