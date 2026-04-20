<?php
// 1. Cargamos tu archivo secreto
$keyFile = 'firebase.json';
if (!file_exists($keyFile)) {
    die("Error: No se encuentra el archivo firebase.json en el servidor.");
}
$key = json_decode(file_get_contents($keyFile), true);
$projectId = $key['project_id'];

// 2. Generamos un Token de seguridad temporal (Requisito de la V1)
$header = json_encode(['alg' => 'RS256', 'typ' => 'JWT']);
$now = time();
$claim = json_encode([
    'iss' => $key['client_email'],
    'scope' => 'https://www.googleapis.com/auth/firebase.messaging',
    'aud' => 'https://oauth2.googleapis.com/token',
    'exp' => $now + 3600,
    'iat' => $now
]);

function base64UrlEncode($text) {
    return str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($text));
}

$base64Header = base64UrlEncode($header);
$base64Claim = base64UrlEncode($claim);
$signatureInput = $base64Header . '.' . $base64Claim;

openssl_sign($signatureInput, $signature, $key['private_key'], "sha256WithRSAEncryption");
$jwt = $signatureInput . '.' . base64UrlEncode($signature);

$ch = curl_init('https://oauth2.googleapis.com/token');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query([
    'grant_type' => 'urn:ietf:params:oauth:grant-type:jwt-bearer',
    'assertion' => $jwt
]));
$response = curl_exec($ch);
curl_close($ch);
$accessToken = json_decode($response, true)['access_token'];

// 3. Enviamos la notificación usando la nueva API V1
$url = 'https://fcm.googleapis.com/v1/projects/' . $projectId . '/messages:send';
$mensaje = [
    'message' => [
        'topic' => 'global', // El canal al que se suscribió tu app
        'notification' => [
            'title' => ' ¡Alerta d eCloapp!',
            'body' => '¡Mensaje enviado mediante php!'
        ]
    ]
];

$ch2 = curl_init($url);
curl_setopt($ch2, CURLOPT_POST, true);
curl_setopt($ch2, CURLOPT_HTTPHEADER, [
    'Authorization: Bearer ' . $accessToken,
    'Content-Type: application/json'
]);
curl_setopt($ch2, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch2, CURLOPT_POSTFIELDS, json_encode($mensaje));
$res2 = curl_exec($ch2);
curl_close($ch2);

echo "<h2>Notificación enviada</h2>";
echo "<p>Respuesta de Google V1: " . $res2 . "</p>";
?>
