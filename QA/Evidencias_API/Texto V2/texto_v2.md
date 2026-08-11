Informe de QA: Diagnóstico y Validación de Integración (Versión 02)
Proyecto: Ecosistema NorthPay 
Estado: Documento de Seguimiento y Actualización Técnica 
Fecha: 29 de mayo de 2026
MÓDULO 01: AUTH / ACCESO
•	Pantalla 1: Prueba de Conexión (Invitaciones)
•	Método: POST
•	URL Completa: https://n4nd0-northpay-backend.hf.space/api/invitations
•	Body (JSON):
•	{ "email": "contratista_prueba@gmail.com" }
• Análisis Técnico de QA: El endpoint responde correctamente a la estrategia de control de acceso. Actúa como un filtro previo (White-list), registrando de forma exitosa los correos autorizados en la base de datos de invitaciones antes de permitir el registro formal.
• Propuesta / Recomendación para el perfil: Se sugiere agregar en el Backend una validación sintáctica estricta para el campo "email" mediante expresiones regulares (Regex), asegurando que no se procesen cadenas vacías o formatos inválidos.
•	Se recomienda documentar claramente este paso para el equipo de Frontend, ya que cualquier intento de registrar un usuario sin invitación previa fallará por diseño.

 

Pantalla 2: Login Usuario
•	Método: POST
•	URL Completa: https://n4nd0-northpay-backend.hf.space/api/auth/login
•	Body (JSON) Real:
{
  "email": "admin@northpay.com",
  "password": "NorthPay123"
}
•     Análisis Técnico de QA: Comportamiento óptimo y conforme a los requisitos de seguridad. Al ingresar credenciales válidas, el servidor responde con un estado 200 OK, retornando un JSON con success: true y entregando el Token de autenticación (Bearer token) junto con el operatorId: 1 para la gestión de sesiones privadas.
•     Propuesta / Recomendación para el perfil:
•	Implementar un límite de intentos de inicio de sesión (Rate Limiting) en el Backend para mitigar posibles ataques de fuerza bruta.
•	Asegurar que el token devuelto viaje con directivas de expiración de tiempo (TTL) acotadas y manejo seguro en el almacenamiento local del cliente (ej. HttpOnly Cookies).
 
Pantalla 3: Registro de Usuario
•	Método: POST
•	URL Completa: https://n4nd0-northpay-backend.hf.space/api/auth/register
•	Body (JSON) Real:
{
  "email": "maratest@gmail.com",
  "password": "Password123!",
  "role": "operator"
}
• Análisis Técnico de QA: Se evidencia un comportamiento esperado de bloqueo con estado 401 Unauthorized y el mensaje "Credenciales requeridas". Esto ocurre porque el correo ingresado (maratest@gmail.com) no ha sido dado de alta previamente a través del flujo de invitaciones. El sistema resguarda correctamente la integridad del acceso.
• Propuesta / Recomendación para el perfil:
•	Mejora de UX/Mensajería: El código de estado 401 es correcto, pero el mensaje "Credenciales requeridas" es ambiguo para el usuario final. Se propone cambiar el mensaje de respuesta del servidor a algo más descriptivo, por ejemplo: "El correo electrónico ingresado no posee una invitación activa en el sistema". Esto evitará confusiones en el desarrollo del Frontend y soporte.
 
MÓDULO 02: ONBOARDING / KYC
•	Pantalla 4: Actualizar Perfil
o	Método: PUT
•	•  URL en Postman: {{url_base}}/api/auth/profile
•	•  URL Real (Entorno de Prueba): https://n4nd0-northpay-backend.hf.space/api/auth/profile
o	Body (JSON) Real:
{
  "dni": "12345678",
  "fecha_nacimiento": "1990-01-01",
  "direccion": "Av. Siempreviva 742",
  "ciudad": "Buenos Aires"
}
• Análisis Técnico de QA: Se evidencia un comportamiento de bloqueo con estado 401 Unauthorized y el mensaje "Credenciales requeridas". Esto ocurre porque la petición PUT requiere autenticación obligatoria mediante un Bearer Token válido en la cabecera. Al no enviarse las credenciales de sesión activa correspondientes, el sistema resguarda de forma correcta la seguridad del endpoint.

• Propuesta / Recomendación para el perfil: 
Validación del flujo de sesión: Asegurar desde el Frontend que el token de autenticación obtenido en el Login (Pantalla 2) sea capturado y persistido correctamente en el almacenamiento del cliente, para luego ser adjuntado de manera automática en las cabeceras de todas las peticiones privadas subsiguientes como la actualización del perfil.
 