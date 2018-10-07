CREATE TABLE estado (
	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO estado (codigo, nome) VALUES(11, 'Rondônia');
INSERT INTO estado (codigo, nome) VALUES(12, 'Acre');
INSERT INTO estado (codigo, nome) VALUES(13, 'Amazonas');
INSERT INTO estado (codigo, nome) VALUES(14, 'Roraima');
INSERT INTO estado (codigo, nome) VALUES(15, 'Pará');
INSERT INTO estado (codigo, nome) VALUES(16, 'Amapá');
INSERT INTO estado (codigo, nome) VALUES(17, 'Tocantins');
INSERT INTO estado (codigo, nome) VALUES(21, 'Maranhão');
INSERT INTO estado (codigo, nome) VALUES(22, 'Piauí');
INSERT INTO estado (codigo, nome) VALUES(23, 'Ceará');
INSERT INTO estado (codigo, nome) VALUES(24, 'Rio Grande do Norte');
INSERT INTO estado (codigo, nome) VALUES(25, 'Paraíba');
INSERT INTO estado (codigo, nome) VALUES(26, 'Pernambuco');
INSERT INTO estado (codigo, nome) VALUES(27, 'Alagoas');
INSERT INTO estado (codigo, nome) VALUES(28, 'Sergipe');
INSERT INTO estado (codigo, nome) VALUES(29, 'Bahia');
INSERT INTO estado (codigo, nome) VALUES(31, 'Minas Gerais');
INSERT INTO estado (codigo, nome) VALUES(32, 'Espírito Santo');
INSERT INTO estado (codigo, nome) VALUES(33, 'Rio de Janeiro');
INSERT INTO estado (codigo, nome) VALUES(35, 'São Paulo');
INSERT INTO estado (codigo, nome) VALUES(41, 'Paraná');
INSERT INTO estado (codigo, nome) VALUES(42, 'Santa Catarina');
INSERT INTO estado (codigo, nome) VALUES(43, 'Rio Grande do Sul');
INSERT INTO estado (codigo, nome) VALUES(50, 'Mato Grosso do Sul');
INSERT INTO estado (codigo, nome) VALUES(51, 'Mato Grosso');
INSERT INTO estado (codigo, nome) VALUES(52, 'Goiás');
INSERT INTO estado (codigo, nome) VALUES(53, 'Distrito Federal');



CREATE TABLE cidade (
	codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
	nome VARCHAR(50) NOT NULL,
  codigo_estado BIGINT(20) NOT NULL,
  FOREIGN KEY (codigo_estado) REFERENCES estado(codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO cidade (codigo, nome, codigo_estado) VALUES ( 5300108, "Brasília", 53);



ALTER TABLE pessoa DROP COLUMN cidade;
ALTER TABLE pessoa DROP COLUMN estado;
ALTER TABLE pessoa ADD COLUMN codigo_cidade BIGINT(20);
ALTER TABLE pessoa ADD CONSTRAINT fk_pessoa_cidade FOREIGN KEY (codigo_cidade) REFERENCES cidade(codigo);

UPDATE pessoa SET codigo_cidade = 5300108;