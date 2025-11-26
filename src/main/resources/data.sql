-- Inserir hotéis de exemplo em São Paulo
INSERT INTO hoteis (nome, cep, endereco, cidade, uf, latitude, longitude, descricao, estrelas) VALUES
('Hotel Transilvania Grand', '01310100', 'Av. Paulista, 1000', 'São Paulo', 'SP', -23.5613, -46.6563, 'Hotel 5 estrelas no coração da Paulista', 5),
('Transilvania Business Hotel', '01310200', 'Av. Paulista, 1500', 'São Paulo', 'SP', -23.5625, -46.6580, 'Hotel corporativo moderno', 4),
('Transilvania Park Hotel', '01311000', 'Rua Augusta, 200', 'São Paulo', 'SP', -23.5640, -46.6530, 'Hotel com vista para o parque', 4),
('Transilvania Express', '01310300', 'Rua da Consolação, 100', 'São Paulo', 'SP', -23.5598, -46.6620, 'Hotel econômico bem localizado', 3),
('Transilvania Suites', '01311100', 'Rua Haddock Lobo, 50', 'São Paulo', 'SP', -23.5655, -46.6545, 'Apart-hotel com cozinha', 4);

-- Inserir hotéis de exemplo no Rio de Janeiro
INSERT INTO hoteis (nome, cep, endereco, cidade, uf, latitude, longitude, descricao, estrelas) VALUES
('Transilvania Copacabana', '22040020', 'Av. Atlântica, 1000', 'Rio de Janeiro', 'RJ', -22.9711, -43.1825, 'Hotel à beira-mar', 5),
('Transilvania Ipanema', '22420040', 'Rua Visconde de Pirajá, 500', 'Rio de Janeiro', 'RJ', -22.9838, -43.2096, 'Hotel boutique em Ipanema', 4),
('Transilvania Centro RJ', '20040030', 'Av. Rio Branco, 200', 'Rio de Janeiro', 'RJ', -22.9003, -43.1803, 'Hotel histórico no centro', 3);

-- Inserir hotéis de exemplo em Minas Gerais
INSERT INTO hoteis (nome, cep, endereco, cidade, uf, latitude, longitude, descricao, estrelas) VALUES
('Transilvania Savassi', '30130100', 'Av. Getúlio Vargas, 1000', 'Belo Horizonte', 'MG', -19.9395, -43.9345, 'Hotel moderno na Savassi', 4),
('Transilvania Pampulha', '31270000', 'Av. Otacílio Negrão de Lima', 'Belo Horizonte', 'MG', -19.8517, -43.9670, 'Hotel próximo à Pampulha', 4);
