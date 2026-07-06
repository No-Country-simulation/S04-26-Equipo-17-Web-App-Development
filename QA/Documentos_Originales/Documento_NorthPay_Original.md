Documento Original
Documento de Especificación del Proyecto: Portal de Gestión de documentoOnboarding y Pagos - NorthPay
1. Resumen del Proyecto
NorthPay requiere la centralización y automatización de su proceso de alta (onboarding) de contratistas remotos internacionales. El objetivo principal es reducir el tiempo de activación de 12 días a menos de 3 días, eliminando la burocracia manual y la dispersión de canales (email, WhatsApp, formularios offline).
2. Objetivos del Sistema
•	Eficiencia Operativa: Automatizar el flujo de validación para reducir tiempos muertos.
•	Centralización: Sustituir canales informales por un único portal web seguro.
•	Visibilidad: Proporcionar al equipo interno un tablero de control con el estado en tiempo real de cada proceso.
3. Perfiles de Usuario
Perfil	Responsabilidades / Acciones
Contratista Remoto	Completar perfil, subir documentación, firmar contratos y configurar métodos de cobro.
Operador Interno	Monitorear solicitudes, validar documentos, solicitar correcciones y activar cuentas.
4. Flujo de Onboarding (5 Pasos Críticos)
1.	Datos Personales: Formulario de registro con información básica y fiscal.
2.	Carga de Documentos: Repositorio para subir identificaciones oficiales y comprobantes.
3.	Firma de Contrato: Integración con firma digital legalmente vinculante.
4.	Configuración de Pago: Selección y validación de cuentas bancarias o billeteras cripto/digitales.
5.	Verificación de Identidad: Proceso final de validación (KYC) antes de la activación.
5. Requerimientos Técnicos y de Negocio
•	Notificaciones: Alertas automáticas vía email/push ante cambios de estado (ej: "Documento Rechazado").
•	Panel de Administración: Listado dinámico con filtros por estado (Pendiente, En Revisión, Activo).
•	Seguridad: Encriptación de documentos sensibles y cumplimiento de normativas de datos internacionales.
6. Plan de Calidad (QA Strategy)
Dada la naturaleza crítica de los pagos y el cumplimiento (compliance), se establecen los siguientes niveles de prueba:
•	Pruebas Funcionales: Validación de cada paso del formulario y de las reglas de negocio del panel de administración.
•	Pruebas de Integración (API Testing): Verificación de la comunicación con servicios de firma digital y proveedores de pago (vía Postman).
•	Pruebas E2E (End-to-End): Simulación del flujo completo desde la invitación al contratista hasta su activación definitiva.
7. Entregables del Proyecto
•	Prototipo: Maqueta interactiva para validación de flujo de usuario (UX).
•	MVP (Producto Mínimo Viable): Versión funcional inicial con los 5 pasos básicos.
•	Documentación Técnica: Manuales de usuario y especificaciones de API.
•	Demo: Presentación del sistema operando en entorno de pruebas.
