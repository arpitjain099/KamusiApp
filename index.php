<?php
function get_client_ip() {
    $ipaddress = '';
    if (getenv('HTTP_CLIENT_IP'))
        $ipaddress = getenv('HTTP_CLIENT_IP');
    else if(getenv('HTTP_X_FORWARDED_FOR'))
        $ipaddress = getenv('HTTP_X_FORWARDED_FOR');
    else if(getenv('HTTP_X_FORWARDED'))
        $ipaddress = getenv('HTTP_X_FORWARDED');
    else if(getenv('HTTP_FORWARDED_FOR'))
        $ipaddress = getenv('HTTP_FORWARDED_FOR');
    else if(getenv('HTTP_FORWARDED'))
       $ipaddress = getenv('HTTP_FORWARDED');
    else if(getenv('REMOTE_ADDR'))
        $ipaddress = getenv('REMOTE_ADDR');
    else
        $ipaddress = 'UNKNOWN';
    return $ipaddress;
}
echo get_client_ip();

//echo curl "curl ipinfo.io/8.8.8.8";
?>

<?php
// Allocate a new cURL handle
echo "ipinfo.io/"+(string)ip2long(get_client_ip());
$ch = curl_init("ipinfo.io/"+(string)get_client_ip());
if (! $ch) {
	die( "Cannot allocate a new PHP-CURL handle" );
}
//echo $ch;
$data = curl_exec($ch);
echo $data;
curl_close($ch);
//echo "dada";

?>