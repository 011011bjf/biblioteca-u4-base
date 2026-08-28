# Cuestionario — Parte A del examen de la Unidad IV

> **Cómo se llena este archivo.** Responda **dentro de este mismo archivo**, debajo de cada pregunta, en el bloque marcado como `**Respuesta:**`. No borre ni reescriba los enunciados: el evaluador compara pregunta por pregunta. No añada ni quite secciones.
>
> **Este archivo se versiona en el repositorio.** Debe existir en la raíz, llamarse exactamente `Cuestionario.md`, y sus respuestas deben llegar por *commits* sucesivos hechos cuando el docente lo indique. Un archivo que aparece completo en un único *commit* al final de la sesión no cumple el protocolo y se trata según el criterio de piso 4 del examen.
>
> Se valora la precisión técnica y la justificación, **no la extensión**. Una respuesta correcta de seis líneas vale más que una página imprecisa. Cuando la pregunta pida referirse al proyecto base, hágalo con nombres concretos de clases o de *endpoints*.

---

## Datos del estudiante

| Campo | Valor                         |
|---|-------------------------------|
| Apellidos y nombres | Figueroa Morales Bryan Javier |
| Número de carnet | 1206395202                    |
| Correo institucional | bfigueroam@uteq.edu.ec        |
| Fecha | 28/08/2026                    |
| URL del repositorio |  https://github.com/011011bjf/biblioteca-u4-base.git                             |

---

## A1. Restricciones de REST aplicadas a un caso concreto — 8 puntos

**a) Enuncie las seis restricciones del estilo arquitectónico REST según Fielding. (3 puntos)**

**Respuesta:
Cliente-Servidor — separación de responsabilidades: el cliente maneja la interfaz de usuario y el servidor los lógica de negocio y el accseo a datos.
Stateless — cada petición del cliente debe contener toda la información necesaria para ser entendida, sin que el servidor guarde informacion del contexto de seciones ni de peticion;
Cacheable — las respuestas debe conocerce si pueden almacenarse en caché, para reutilizarse en peticiones a la bd.
Interfaz uniforme — identificación de recursos por URI, manipulación mediante representaciones.
Sistema en capas — el cliente no conoce el destino final de la peticion ya que pueden haber intermediarios.
Código bajo demanda (opcional) — el servidor puede extender la funcionalidad del cliente
**



**b) El proyecto base expone `GET /api/v1/autores` y guarda el estado de la sesión del usuario solo en el JWT que el cliente envía en cada petición. Explique qué restricción concreta se está cumpliendo con esa decisión y qué consecuencia práctica tiene para escalar el sistema a varios servidores detrás de un balanceador. (3 puntos)**

**Respuesta:
Se cumple la restricción Stateless. Al no guardar sesión en el servidor y exigir que
el cliente envíe el JWT completo (con toda la información de autenticación/autorización)
en cada petición, ninguna petición depende de un contexto previo almacenado en memoria del servidor.

Consecuencia práctica : cualquier instancia del servidor detrás del balanceador puede atender cualquier petición,
porque no hay necesidad de un almacén de sesión compartido (Redis, memoria distribuida, etc.).
Esto permite escalado horizontal simple: se agregan o quitan réplicas libremente y el balanceador puede repartir peticiones con cualquier
algoritmo sin romper la autenticación del usuario.
**



**c) De las seis restricciones, indique cuál es opcional y dé un ejemplo real de una API que la use. (2 puntos)**

**Respuesta:
que sea cacheable es opcional ya que los datos viven permanente mente en la base de datos y se utiliza la memoria cache
para mejorar en velocidad de respuesta y para guardar datos temporales en algunos casos
como ejemplo en el proyecto fin de curso se applica la memoria cache para el guardado de seciones y tokens , ademas de el catalogo de
servicios que brinda nuestro proyecto. digo que es opcional porque tambien se puede implementar si memora cache y el programa puede funcionar aunque con menos velocidad como lo gestiona la cache
**



---

## A2. Anatomía y ciclo de vida de un JWT — 8 puntos

**a) Un JWT tiene tres partes separadas por puntos. Nómbrelas en orden e indique qué contiene cada una. (3 puntos)**

**Respuesta:
Header: metadatos del token — tipo (`JWT`) y algoritmo de firma usado, codificado en Base64Url.
Payload: los claims — datos del usuario y metadatos del token.
Signature: resultado de aplicar el algoritmo indicado en el header sobre header + payload usando una clave secreta o privada; permite verificar integridad y autenticidad.
**



**b) Un compañero afirma: «como el JWT va firmado, puedo guardar en el *payload* la contraseña del usuario sin riesgo». Explique por qué está equivocado, precisando la diferencia entre firmar y cifrar. (2 puntos)**

**Respuesta:
Firmar ( JWT estándar) garantiza integridad (el contenido no fue alterado) y autenticidad (viene de quien dice venir), pero no oculta el contenido. El header y el payload solo están codificados en Base64Url, que es reversible por cualquiera, no es cifrado.
Cifrar ( JWE) transforma el contenido para que sea ilegible sin una clave de descifrado, garantizando confidencialidad.
Por lo tanto, cualquiera que intercepte el token puede decodificar el payload y leer la contraseña en texto claro. Nunca deben colocarse datos sensibles (contraseñas, números de tarjeta, etc.) en el payload de un JWT firmado sin cifrar.
**




**c) El JWT es *stateless* por diseño, lo que genera un problema conocido: no se puede invalidar un token antes de que expire. Describa dos estrategias distintas para revocarlo y señale la desventaja de cada una. (3 puntos)**

**Respuesta:
Lista negra de tokens revocados, almacenada del lado del servidor usando el jti (identificador único del token) hasta su expiración natural.
Desventaja: reintroduce estado en el servidor, lo cual contradice parcialmente la naturaleza stateless del JWT; requiere una consulta adicional en cada petición, añadiendo latencia.
Tokens de acceso de vida corta + refresh token verificable en base de datos. El access token expira rápido; para renovarlo se usa un refresh token que sí se valida contra un registro persistente que puede revocarse.
Desventaja: no permite invalidar de forma inmediata un access token ya emitido ; añade complejidad de manejar y almacenar dos tipos de token.

**



---

## A3. SOAP frente a REST — 8 puntos

**a) Complete la tabla comparativa con seis criterios entre SOAP y REST. (5 puntos)**

**Respuesta:**

| Criterio | SOAP                                   | REST                                                                         |
|---|----------------------------------------|------------------------------------------------------------------------------|
| Formato del mensaje | solo XML                               | es fleximble, mayormente usa json                                            |
| Contrato de descripción | si utiliza WSDL, formal y obligatorio  | no es obligatorio , se puede utilizar oppenApi como opcional                 |
| Sobrecarga de serialización | XML verboso + cabeceras del sobre SOAP | todo dentro del Json                                                         |
| Tipado | Fuerte, validado por contarto XSD      | depende de como se implemente en el servidor y como se consuma en el cliente |
| Facilidad de consumo desde un cliente móvil |                                        | caualquier plataforma recibe Json                                            |
| Manejo de errores | lo maneja el mismo xml     | mediante codigos html de estados                              |

**b) El Servicio de Rentas Internas del Ecuador expone la autorización de comprobantes electrónicos mediante servicios SOAP. Explique dos razones técnicas por las que una institución de ese tipo mantiene SOAP en lugar de migrar a REST. (3 puntos)**

**Respuesta:

1. Contrato formal con tipado estricto (WSDL + XSD): para documentos legales/tributarios como comprobantes electrónicos,
se necesita una validación estructural inequívoca y verificable automáticamente; el esquema XSD deja cero ambigüedad
sobre los tipos y la estructura de cada campo, algo que un JSON Schema (opcional en REST) no garantiza con el mismo nivel
de obligatoriedad.

2. Seguridad a nivel de mensaje SOAP permite firmar y/o cifrar partes específicas del mensaje XML (no solo el canal de transporte
como hace TLS), lo que da no repudio y trazabilidad legal por documento — un requisito crítico cuando cada comprobante debe
poder auditarse individualmente años después, independientemente del canal por el que viajó.

**



---

## A4. Cache-aside sobre un servicio externo — 8 puntos

> El proyecto base define en `CacheConfig` dos espacios de caché: `libros` con TTL de 2 minutos y `openlibrary` con TTL de 24 horas.

**a) Describa el patrón *cache-aside* en sus cuatro pasos, desde que llega la petición hasta que se responde. (3 puntos)**

**Respuesta:
1. Llega la petición a la aplicación.
2. La aplicación consulta primero la caché con la petición.
3. Si hay, se devuelve el dato directamente desde la caché. Si no se consulta la fuente real.
4. El resultado obtenido de la fuente real se guarda en la caché (con su TTL) y luego se responde al cliente.
**



**b) Justifique técnicamente por qué el TTL de `openlibrary` es doce veces mayor que el de `libros`, y qué criterio general debe guiar la elección de un TTL. (3 puntos)**

**Respuesta:
Los datos de OpenLibrary(metadatos) son prácticamente estáticos — cambian con muy
poca frecuencia y consultarlos tiene costo/latencia alto (API externa, con límites de tasa). Por eso conviene un TTL largo (24h):
reduce drásticamente las llamadas al servicio externo sin riesgo real de mostrar datos desactualizados.

En cambio, `libros` refleja estado interno del propio sistema que cambia constantemente
por operaciones , así que necesita un TTL corto para no mostrar disponibilidad incorrecta.

**



**c) Explique por qué nunca debe almacenarse en caché la respuesta de un fallo del servicio externo, y describa qué le ocurriría al sistema si se hiciera. (2 puntos)**

**Respuesta:

Si se cachea un error (timeout, 500, etc.) del servicio externo, todas las peticiones siguientes durante el TTL recibirán ese mismo
error, aunque el servicio externo ya se haya recuperado. En la práctica, el propio sistema prolongaría artificialmente una caída que
ya terminó — se autoinfligiría una interrupción de servicio durante todo el TTL configurado, incluso después de que el problema
original ya no existiera.

**



---

## A5. Diagnóstico de códigos de estado y contrato de errores — 8 puntos

> Todos los errores del proyecto base salen en formato *Problem Details* conforme a la RFC 9457, que obsoleta a la RFC 7807.

Para cada escenario indique el código HTTP correcto y explique en una línea por qué. **Cada fila vale 1 punto** (0,5 por el código y 0,5 por la justificación); el literal g) vale 2 puntos.

| # | Escenario | Código | Justificación (una línea) |
|---|---|---|---|
| a) `GET /libros/999999` no existe | 404 Not Found | El recurso identificado por esa URI no existe en el servidor. |
| b) `POST /libros` sin `Authorization` | 401 Unauthorized | No se puede verificar la identidad del cliente porque falta la credencial. |
| c) Usuario `LECTOR` intenta `POST /libros` | 403 Forbidden | El cliente está autenticado pero no tiene permisos suficientes para esa acción. |
| d) `POST /libros` con `titulo` vacío | 400 Bad Request| La petición está sintácticamente bien formada pero incumple las reglas de validación del recurso enviado. |
| e) Socio con 3 préstamos activos pide otro | 409 Conflict | El estado actual del recurso (límite de préstamos alcanzado) impide procesar la solicitud aunque esté bien formada. |
| f) OpenLibrary no responde a tiempo | 504 Gateway Timeout | El servidor actúa como intermediario hacia un servicio externo (capa) que no respondió dentro del tiempo esperado. |

**g) Explique por qué devolver `200 OK` con un cuerpo `{"success": false}` es un error de diseño, y qué restricción de REST se incumple al hacerlo. (2 puntos)**

**Respuesta:
Porque desacopla el resultado real de la operación del código de estado HTTP: obliga a todo cliente a parsear el cuerpo para saber si
la operación tuvo éxito, y rompe el comportamiento de intermediarios (proxies, cachés, herramientas de monitoreo) que interpretan
`200` como éxito por convención y podrían cachear o dar por buena una respuesta que en realidad fue un fallo.

Esto incumple la restricción de Interfaz uniforme, específicamente el principio de mensajes autodescriptivos: el metadato
debe describir por sí mismo cómo procesar el mensaje y su resultado, sin que un componente intermedio
tenga que inspeccionar el cuerpo para entender el resultado real de la operación.

**



---

## Declaración de honestidad académica

Marque con una `x` y complete:

- [x] Declaro que estas respuestas son de mi autoría, redactadas durante la sesión de examen, sin asistencia de inteligencia artificial ni comunicación con terceros.

Firma (nombre completo): Bryan Javier Figueroa Morales
