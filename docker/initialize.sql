CREATE DATABASE JavaFX;
GO

CREATE TABLE JavaFX.dbo.Usuario (
    id        INT           IDENTITY(1,1) PRIMARY KEY,
    nome      NVARCHAR(100) NOT NULL,
    email     NVARCHAR(150) NOT NULL UNIQUE,
    senha     NVARCHAR(255) NOT NULL,
    criado_em DATETIME      DEFAULT GETDATE()
);
GO

CREATE TABLE JavaFX.dbo.Produto (
    id        INT            IDENTITY(1,1) PRIMARY KEY,
    nome      NVARCHAR(100)  NOT NULL,
    preco     DECIMAL(10, 2) NOT NULL,
    estoque   INT            NOT NULL DEFAULT 0,
    criado_em DATETIME       DEFAULT GETDATE()
);
GO
