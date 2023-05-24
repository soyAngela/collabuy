<?php
#Se recoge la lista de tokens
$tokens = $_POST["tokens"];
$nombreGrupo = $_POST["nombreGrupo"];

#Se define la cabecera la clave del servicio firebase
$cabecera= array(
'Authorization: key=AAAArzrdNOc:APA91bEqUmwSyIf-TXFwxE9NOMR6628vVaBgZe11ZtBE5W-Ibags4UhqOi0kj-SG-ZZq65Il_3eAj7wGlVqYFewS_jkJtcvwcW3MBfDj2zbyd7bgZBVCp9abJv-WRu0anarwEWqrQujd',
'Content-Type: application/json',
'project_id: 752606852327'
);
#El mensaje con sus respectivos datos y notificación
$msg = array (
    'operation'=>"create",
    'notification_key_name' =>"$nombreGrupo",
    'registration_ids'=>json_decode($tokens) 
);
#Se devuelven los datos en formato json
$msgJSON= json_encode($msg);

$ch = curl_init(); #inicializar el handler de curl
#indicar el destino de la petición, el servicio FCM de google
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/notification');
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