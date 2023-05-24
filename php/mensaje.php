<?php
#Se recogen todos los parametros
$mensaje = $_POST["mensaje"];
$nK = $_POST["nK"];
$idLista = $_POST["idLista"];
$nombreLista = $_POST["nombreLista"];

#Se define la cabecera la clave del servicio firebase
$cabecera= array(
'Authorization: key=AAAArzrdNOc:APA91bEqUmwSyIf-TXFwxE9NOMR6628vVaBgZe11ZtBE5W-Ibags4UhqOi0kj-SG-ZZq65Il_3eAj7wGlVqYFewS_jkJtcvwcW3MBfDj2zbyd7bgZBVCp9abJv-WRu0anarwEWqrQujd',
'Content-Type: application/json'
);
#El mensaje con sus respectivos datos y notificación
$msg = array (
    'to' => "$nK",
    'data' => array (
    "idLista" => "$idLista",
    "nombreLista" => "$nombreLista",
    "notificacion" => "1"),
    'notification' => array (
    "body" => "$mensaje",
    "title" => "¡Nuevo producto!",
    "icon" => "ic_stat_ic_notification",
    "click_action"=>"AVISO"
    )
);
#Se devuelven los datos en formato json
$msgJSON= json_encode($msg);

$ch = curl_init(); #inicializar el handler de curl
#indicar el destino de la petición, el servicio FCM de google
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
#indicar que la conexión es de tipo POST
curl_setopt( $ch, CURLOPT_POST, true );
#agregar las cabeceras
curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
#Indicar que se desea recibir la respuesta a la conexión en forma de string
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
#agregar los datos de la petición en formato JSON
curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );
#ejecutar la llamada
$resultado= curl_exec( $ch );
#cerrar el handler de curl
curl_close( $ch );
if (curl_errno($ch)) {
    print curl_error($ch);
}
echo $resultado;
?>