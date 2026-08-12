HISTORIAS DE USUARIOS Y CRITERIOS DE ACEPTACION
Épica 1: Sistema de Gestión de Invitaciones y Acceso Seguro
HU #01: Acceso al portal mediante Token Seguro
•	Descripción: Como contratista autorizado, quiero recibir un correo con un token único y temporal para acceder al portal de onboarding de forma segura sin usar contraseñas tradicionales. 
•	Criterios de Aceptación (CA):
1.	El sistema debe generar un token único y un Magic Link que se envíe vía email. 
2.	El token debe tener una validez estricta de 24 horas; después de ese tiempo, el acceso debe denegarse. 
3.	Al hacer clic en el link, el sistema debe autenticar al usuario mediante JWT para iniciar la sesión. 

Épica 2: Experiencia de Onboarding Autogestionado
HU #02: Carga de Datos Personales y Documentación
•	Descripción: Como contratista, quiero completar mis datos personales y subir mis documentos de identidad (DNI/Pasaporte) para que mi perfil pueda ser validado por la administración. 
•	Criterios de Aceptación (CA):
1.	El formulario debe capturar campos obligatorios: Nombre, Apellido, Email y País. 
2.	El módulo de archivos debe permitir formatos PDF, JPG y PNG. 
3.	Los archivos deben almacenarse de forma segura en Supabase Storage. 
4.	El usuario no puede avanzar a la siguiente sección sin completar todos los campos obligatorios. 
HU #03: Firma de Contrato y Configuración de Pagos
•	Descripción: Como contratista, quiero visualizar y firmar mi contrato digitalmente y configurar mis métodos de pago para formalizar mi relación con la empresa. 
•	Criterios de Aceptación (CA):
1.	El portal debe mostrar el contrato legal generado dinámicamente con los datos del usuario. 
2.	El usuario debe poder seleccionar entre Transferencia Bancaria o Wallet Cripto. 
3.	El sistema debe registrar la firma digital y cambiar el estado del onboarding a "Pending Review". 
Épica 3: Panel de Control y Operaciones (Backoffice)
HU #04: Gestión y Validación de Onboardings
•	Descripción: Como operador administrativo, quiero visualizar el listado de contratistas pendientes y revisar su documentación para aprobar o rechazar su activación en el sistema. 
•	Criterios de Aceptación (CA):
1.	El panel debe permitir filtrar contratistas por país y por estado (Pendiente, Aprobado, Rechazado). 
2.	El operador debe tener botones de "Aprobar" y "Solicitar Corrección". 
3.	Cada acción de aprobación o rechazo debe quedar registrada en el log de auditoría del sistema. 
Épica 4: Motor de Estados y Notificaciones
HU #05: Notificaciones Automáticas de Estado
•	Descripción: Como usuario del sistema (contractor o admin), quiero recibir correos automáticos cuando mi estado cambie para estar informado sobre el progreso del proceso. 
•	Criterios de Aceptación (CA):
1.	El sistema debe enviar un correo vía Brevo SMTP cuando el administrador apruebe o rechace un documento. 
2.	Las transiciones de estado (ej: de "Pending" a "Activated") deben ser automáticas tras la validación final. 
Épica 5: Infraestructura Tecnológica y Seguridad (Fundación del Sistema)
HU #06: Configuración del Entorno de Desarrollo y Persistencia
•	Descripción: Como equipo de desarrollo, queremos configurar el entorno de Backend en Java y la base de datos en Supabase para tener una infraestructura robusta que soporte las operaciones del MVP. 
•	Criterios de Aceptación (CA):
1.	El proyecto de Backend debe estar inicializado en Java con Spring Boot (o el framework acordado). 
2.	La conexión con la base de datos de Supabase debe estar establecida y verificada. 
3.	El esquema inicial de tablas (Usuarios, Tokens, Onboarding) debe estar desplegado en Supabase. 
HU #07: Implementación de Almacenamiento Seguro de Archivos
•	Descripción: Como administrador del sistema, quiero que el almacenamiento de documentos se realice en Supabase Storage para garantizar la integridad y disponibilidad de la documentación de los contratistas. 
•	Criterios de Aceptación (CA):
1.	El "Bucket" de almacenamiento en Supabase Storage debe estar configurado y accesible desde el Backend. 
2.	Se deben implementar políticas de acceso (RLS - Row Level Security) para que solo usuarios autorizados vean sus documentos. 
3.	La subida y descarga de archivos (PDF/JPG) debe funcionar mediante la API del sistema. 
HU #08: Encriptación y Seguridad de Datos Sensibles
•	Descripción: Como responsable de cumplimiento (Compliance), quiero que los datos personales y documentos sensibles estén encriptados para asegurar la privacidad de los contratistas y cumplir con normativas de seguridad. 
•	Criterios de Aceptación (CA):
1.	Todos los documentos en Supabase Storage deben estar protegidos mediante encriptación en reposo. 
2.	Los datos sensibles en la base de datos (como tokens o información financiera) deben estar cifrados. 
3.	Todas las comunicaciones entre el Frontend y el Backend deben realizarse bajo protocolo seguro HTTPS. 


