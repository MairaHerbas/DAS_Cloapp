<?php
// 1. Recibimos el nombre de la prenda desde Android (si no llega, ponemos un texto por defecto)
$nombrePrenda = isset($_GET['prenda']) ? $_GET['prenda'] : 'Una nueva prenda';

// 2. Cargamos tu archivo secreto
$keyFile = 'firebase.json';
if (!file_exists($keyFile)) {
    die("Error: No se encuentra firebase.json.");
}
$key = json_decode(file_get_contents($keyFile), true);
$projectId = $key['project_id'];

// 3. Generamos el Token de seguridad V1
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

$signatureInput = base64UrlEncode($header) . '.' . base64UrlEncode($claim);
openssl_sign($signatureInput, $signature, $key['private_key'], "sha256WithRSAEncryption");
$jwt = $signatureInput . '.' . base64UrlEncode($signature);

$ch = curl_init('https://oauth2.googleapis.com/token');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(['grant_type' => 'urn:ietf:params:oauth:grant-type:jwt-bearer', 'assertion' => $jwt]));
$accessToken = json_decode(curl_exec($ch), true)['access_token'];
curl_close($ch);

// 4. Enviamos la notificación personalizada a la app
$url = 'https://fcm.googleapis.com/v1/projects/' . $projectId . '/messages:send';
$mensaje = [
    'message' => [
        'topic' => 'global',
        'notification' => [
            'title' => '👕 ¡Armario Actualizado!',
            'body' => 'Has añadido con éxito: ' . $nombrePrenda
        ]
    ]
];

$ch2 = curl_init($url);
curl_setopt($ch2, CURLOPT_POST, true);
curl_setopt($ch2, CURLOPT_HTTPHEADER, ['Authorization: Bearer ' . $accessToken, 'Content-Type: application/json']);
curl_setopt($ch2, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch2, CURLOPT_POSTFIELDS, json_encode($mensaje));
$res2 = curl_exec($ch2);
curl_close($ch2);

echo "Enviado: " . $nombrePrenda;
?>
