INSERT INTO restaurante (id, cep, complemento, name) VALUES 
(1L, '0000001', 'Complemento Endereço Restaurante 1', 'Restaurante 01'),
(2L, '0000002', 'Complemento Endereço Restaurante 2', 'Restaurante 02');

INSERT INTO cliente (id, cep, complemento, name) VALUES 
(1L, '0000001', 'Complemento Endereço Cliente 1', 'Cliente 01');

INSERT INTO produto (id, disponivel, name, valor_unitario, restaurante_id) VALUES
(1L, true, 'Produto 01', 5.0, 1L),
(2L, true, 'Produto 02', 6.0, 1L),
(3L, true, 'Produto 03', 15.0, 2L);

INSERT INTO sacola (id, forma_pagamento, fechada, valor_total, cliente_id) VALUES
(1L, 0, false, 0.0, 1L);
