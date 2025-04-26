CREATE TABLE Usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    correo VARCHAR(50) UNIQUE,
    contraseña VARCHAR(30)
);

CREATE TABLE Sucursal (
    id_sucursal SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    direccion VARCHAR(30),
    id_responsable INT
);

CREATE TABLE Staff (
    id_staff SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    id_sucursal INT REFERENCES Sucursal(id_sucursal)
);

-- Agregar restricción de clave foránea después de crear Staff
ALTER TABLE Sucursal
ADD CONSTRAINT fk_responsable FOREIGN KEY (id_responsable) REFERENCES Staff(id_staff);

CREATE TABLE Producto (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    descripcion TEXT,
    precio_compra NUMERIC(10,2),
    precio_venta NUMERIC(10,2),
    id_sucursal INT REFERENCES Sucursal(id_sucursal)
);

CREATE TABLE Inventario (
    id_inventario SERIAL PRIMARY KEY,
    id_producto INT REFERENCES Producto(id_producto),
    cantidad INT,
    cantidad_vendida INT DEFAULT 0,
    fecha_ultimo_movimiento DATE
);

CREATE TABLE Notificacion (
    id_notificacion SERIAL PRIMARY KEY,
    mensaje TEXT,
    tipo VARCHAR(50),
    fecha DATE,
    id_producto INT REFERENCES Producto(id_producto)
);

CREATE TABLE Transferencia (
    id_transferencia SERIAL PRIMARY KEY,
    id_producto INT REFERENCES Producto(id_producto),
    sucursal_origen INT REFERENCES Sucursal(id_sucursal),
    sucursal_destino INT REFERENCES Sucursal(id_sucursal),
    cantidad INT,
    fecha DATE
);

CREATE TABLE Cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(50),
    ciudad VARCHAR(50)
);
