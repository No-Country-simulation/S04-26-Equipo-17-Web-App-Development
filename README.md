NorthPay Onboarding Portal
## Descripción del Proyecto
NorthPay es una plataforma diseñada para centralizar y automatizar el proceso de onboarding de contratistas remotos internacionales. El sistema reduce significativamente los tiempos de activación (de 12 días a menos de 3) al eliminar procesos manuales, burocracia y la dispersión de canales de comunicación informales.

## Stack Tecnológico
Backend: Spring Boot (MVC con arquitectura por funcionalidades).

Base de Datos: PostgreSQL gestionado en Supabase (Puerto 6543 para pooler).

Infraestructura: Despliegue en Hugging Face Spaces.

Integraciones:

Brevo: Notificaciones SMTP transaccionales.

Pusher: Alertas en tiempo real.

Supabase Storage: Gestión de documentos sensibles (Límite 6MB).

QA & testing: Postman (API Testing), validación de endpoints y auditoría de protocolos.

## Características Principales
Flujo de Onboarding (5 Pasos Críticos)
Datos Personales: Registro y validación fiscal.

Carga de Documentos: Repositorio seguro para identificaciones.

Firma de Contrato: Integración con firma digital vinculante.

Configuración de Pago: Validación de cuentas bancarias/crypto.

Verificación de Identidad (KYC): Validación biométrica final.

Módulos de Control
Dashboard Operativo: Visualización del estado en tiempo real (SLA).

Gestión de Correcciones: Flujo en cascada para rechazo técnico con feedback directo.

Trazabilidad: Auditoría completa de eventos y cambios de estado.

# Calidad y Testing (QA)
Este proyecto ha sido auditado mediante un protocolo riguroso de QA enfocado en la estabilidad de la API:

Pruebas Funcionales: Validación de reglas de negocio en los 5 pasos de onboarding.

API Testing: Auditoría de endpoints y cabeceras de autorización (Bearer <token>).

Optimización: Resolución de incidencias de comunicación HTTP/2 y optimización de latencia en peticiones mediante endpoints unificados.

Nota: Se ha implementado una estrategia de mitigación de riesgos que incluye la centralización de transiciones en StateMachineService y una arquitectura preparada para auditoría avanzada.

# Perfiles de Usuario
Contratista Remoto: Completa su perfil, sube documentos, firma contratos y configura sus métodos de cobro.

Operador Interno: Monitorea solicitudes, gestiona correcciones y activa las cuentas definitivas mediante el panel de administración.

# Roadmap & Entregables
[x] MVP: Flujo funcional básico de 5 pasos.

[x] Documentación Técnica: Especificaciones de API y manuales.

[x] Protocolo QA: Documentación de casos de prueba (CP) y reportes de diagnóstico.

# Notas de Seguridad
Autenticación: Implementada mediante login con email y contraseña (hash BCrypt). No se requiere Spring Security complejo para esta fase MVP, garantizando agilidad en el despliegue.

Cumplimiento: Encriptación de documentos sensibles y manejo estricto de roles (ADMIN_OP).

# Equipo de Desarrollo & QA
Este proyecto es el resultado de un esfuerzo colaborativo enfocado en la eficiencia operativa y la calidad de software.

Reportes de QA realizados por: Zulay Peraza.

Arquitectura Backend: Equipo de 3 desarrolladores.

Documentación generada bajo el estándar de Ecosistema NorthPay v1.0 (Mayo 2026).
