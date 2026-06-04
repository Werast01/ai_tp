-- Очистка данных (без удаления справочных таблиц)
TRUNCATE TABLE scheme1.trips RESTART IDENTITY CASCADE;
TRUNCATE TABLE scheme1.technical_inspections RESTART IDENTITY CASCADE;
TRUNCATE TABLE scheme1.repairs RESTART IDENTITY CASCADE;
TRUNCATE TABLE scheme1.drivers RESTART IDENTITY CASCADE;
TRUNCATE TABLE scheme1.client_requests RESTART IDENTITY CASCADE;
TRUNCATE TABLE scheme1.clients RESTART IDENTITY CASCADE;
TRUNCATE TABLE scheme1.cars RESTART IDENTITY CASCADE;
TRUNCATE TABLE scheme1.addresses RESTART IDENTITY CASCADE;
TRUNCATE TABLE scheme1.workers RESTART IDENTITY CASCADE;

-- Очистка последовательностей для всех таблиц (кроме справочных)
ALTER SEQUENCE scheme1.addresses_id_seq RESTART WITH 1;
ALTER SEQUENCE scheme1.cars_id_seq RESTART WITH 1;
ALTER SEQUENCE scheme1.clients_id_seq RESTART WITH 1;
ALTER SEQUENCE scheme1.workers_id_seq RESTART WITH 1;
ALTER SEQUENCE scheme1.drivers_id_seq RESTART WITH 1;
ALTER SEQUENCE scheme1.client_requests_id_seq RESTART WITH 1;
ALTER SEQUENCE scheme1.repairs_id_seq RESTART WITH 1;
ALTER SEQUENCE scheme1.technical_inspections_id_seq RESTART WITH 1;
ALTER SEQUENCE scheme1.trips_id_seq RESTART WITH 1;

-- Заполнение addresses (10 адресов)
INSERT INTO scheme1.addresses (town, street, house, created_by) VALUES
('Минск', 'ул. Независимости', '12', 'admin'),
('Минск', 'пр. Победителей', '45', 'admin'),
('Гомель', 'ул. Советская', '7', 'admin'),
('Могилёв', 'пр. Мира', '23', 'admin'),
('Витебск', 'ул. Ленина', '56', 'admin'),
('Гродно', 'ул. Горького', '89', 'admin'),
('Брест', 'ул. Московская', '34', 'admin'),
('Минск', 'ул. Притыцкого', '101', 'admin'),
('Минск', 'пр. Дзержинского', '67', 'admin'),
('Бобруйск', 'ул. Социалистическая', '18', 'admin');

-- Заполнение cars (10 автомобилей)
INSERT INTO scheme1.cars (marka, model, "gosNumber", "yearOfManufacture", "vinNumber", insurance, "conditionId", capacity, created_by) VALUES
('Toyota', 'Camry', 'А123ВС', 2020, 'JTDBE30K000000001', 'Страхование А', 1, 4, 'admin'),
('Renault', 'Logan', 'В456МР', 2019, 'VF1LMB00000000002', 'Страхование Б', 2, 4, 'admin'),
('Volkswagen', 'Passat', 'С789ТЕ', 2021, 'WVWZZZ3CZ00000003', 'Страхование В', 1, 4, 'admin'),
('BMW', 'X5', 'Е123АК', 2018, 'WBAKS410000000004', 'Страхование Г', 3, 5, 'admin'),
('Mercedes', 'E200', 'К456НО', 2022, 'WDD21300000000005', 'Страхование А', 1, 4, 'admin'),
('Kia', 'Rio', 'М789АВ', 2020, 'KNADN000000000006', 'Страхование Б', 2, 4, 'admin'),
('Hyundai', 'Solaris', 'О123ЕК', 2019, 'KMHDN000000000007', 'Страхование В', 2, 4, 'admin'),
('Ford', 'Focus', 'Р456ВС', 2017, 'WF0FXX00000000008', 'Страхование Г', 2, 4, 'admin'),
('Nissan', 'Qashqai', 'С789КР', 2021, 'JN1BJ000000000009', 'Страхование А', 1, 5, 'admin'),
('Lada', 'Vesta', 'Т123ММ', 2020, 'XTA21000000000010', 'Страхование Б', 2, 4, 'admin');

-- Заполнение clients (10 клиентов)
INSERT INTO scheme1.clients (name, familya, "lastName", "phoneNumber", email, created_by) VALUES
('Иван', 'Иванов', 'Петрович', '+375291234567', 'ivanov@mail.com', 'admin'),
('Мария', 'Петрова', 'Сергеевна', '+375292345678', 'petrova@mail.com', 'admin'),
('Алексей', 'Сидоров', 'Андреевич', '+375293456789', 'sidorov@mail.com', 'admin'),
('Елена', 'Козлова', 'Владимировна', '+375294567890', 'kozlova@mail.com', 'admin'),
('Дмитрий', 'Соколов', 'Игоревич', '+375295678901', 'sokolov@mail.com', 'admin'),
('Ольга', 'Михайлова', 'Александровна', '+375296789012', 'mikhailova@mail.com', 'admin'),
('Сергей', 'Новиков', 'Витальевич', '+375297890123', 'novikov@mail.com', 'admin'),
('Татьяна', 'Фёдорова', 'Павловна', '+375298901234', 'fedorova@mail.com', 'admin'),
('Андрей', 'Морозов', 'Денисович', '+375299012345', 'morozov@mail.com', 'admin'),
('Наталья', 'Волкова', 'Романовна', '+375291123456', 'volkova@mail.com', 'admin');


-- Заполнение workers (10 работников)
INSERT INTO scheme1.workers (name, familya, "lastName", "phoneNumber", email, "positionId", created_by) VALUES
('Михаил', 'Кузнецов', 'Викторович', '+375441234567', 'kuznecov@company.com', 3, 'admin'),
('Антон', 'Васильев', 'Сергеевич', '+375442345678', 'vasiliev@company.com', 3, 'admin'),
('Сергей', 'Павлов', 'Алексеевич', '+375443456789', 'pavlov@company.com', 3, 'admin'),
('Денис', 'Степанов', 'Владимирович', '+375444567890', 'stepanov@company.com', 1, 'admin'),
('Владимир', 'Николаев', 'Петрович', '+375445678901', 'nikolaev@company.com', 3, 'admin'),
('Александр', 'Цветков', 'Иванович', '+375446789012', 'cvetkov@company.com', 1, 'admin'),
('Павел', 'Григорьев', 'Николаевич', '+375447890123', 'grigoriev@company.com', 1, 'admin'),
('Олег', 'Тимофеев', 'Викторович', '+375448901234', 'timofeev@company.com', 1, 'admin'),
('Роман', 'Филиппов', 'Андреевич', '+375449012345', 'phillipov@company.com', 2, 'admin'),
('Евгений', 'Кондратьев', 'Сергеевич', '+375441123456', 'kondratev@company.com', 1, 'admin');

-- Заполнение drivers (10 водителей)
INSERT INTO scheme1.drivers ("workerId", license, created_by) VALUES
(2, 'AB1234567', 'admin'),
(3, 'BC2345678', 'admin'),
(6, 'CD3456789', 'admin'),
(9, 'DE4567890', 'admin'),
(2, 'EF5678901', 'admin'),
(3, 'FG6789012', 'admin'),
(6, 'GH7890123', 'admin'),
(9, 'HI8901234', 'admin'),
(2, 'IJ9012345', 'admin'),
(3, 'JK0123456', 'admin');

-- Заполнение client_requests (10 заявок)
INSERT INTO scheme1.client_requests ("executionDate", "clientId", "numberOfPersons", "startPoint", "endPoint", created_by) VALUES
('2024-12-01', 1, 2, 1, 2, 'admin'),
('2024-12-02', 2, 3, 2, 3, 'admin'),
('2024-12-03', 3, 1, 3, 4, 'admin'),
('2024-12-04', 4, 4, 4, 5, 'admin'),
('2024-12-05', 5, 2, 5, 6, 'admin'),
('2024-12-06', 6, 3, 6, 7, 'admin'),
('2024-12-07', 7, 5, 7, 8, 'admin'),
('2024-12-08', 8, 2, 8, 9, 'admin'),
('2024-12-09', 9, 3, 9, 10, 'admin'),
('2024-12-10', 10, 4, 1, 10, 'admin');

-- Заполнение repairs (10 ремонтов)
INSERT INTO scheme1.repairs ("carId", "mechanicId", "repairDate", result, created_by) VALUES
(1, 1, '2024-11-01', 'Замена масла и фильтров', 'admin'),
(2, 4, '2024-11-05', 'Ремонт тормозной системы', 'admin'),
(3, 1, '2024-11-10', 'Диагностика двигателя', 'admin'),
(4, 8, '2024-11-15', 'Замена резины', 'admin'),
(5, 1, '2024-11-20', 'Замена ГРМ', 'admin'),
(6, 4, '2024-11-25', 'Ремонт подвески', 'admin'),
(7, 8, '2024-11-30', 'Замена ремня', 'admin'),
(8, 1, '2024-12-01', 'Замена стёкол', 'admin'),
(9, 4, '2024-12-02', 'Покраска кузова', 'admin'),
(10, 8, '2024-12-03', 'Ремонт кондиционера', 'admin');

-- Заполнение technical_inspections (10 техосмотров)
INSERT INTO scheme1.technical_inspections ("carId", "inspectionDate", result, "mechanicId", created_by) VALUES
(1, '2024-10-15', 'Исправен', 8, 'admin'),
(2, '2024-10-20', 'Требуется ремонт тормозов', 1, 'admin'),
(3, '2024-10-25', 'Исправен', 4, 'admin'),
(4, '2024-11-01', 'Исправен', 8, 'admin'),
(5, '2024-11-05', 'Требуется замена масла', 1, 'admin'),
(6, '2024-11-10', 'Исправен', 4, 'admin'),
(7, '2024-11-15', 'Исправен', 8, 'admin'),
(8, '2024-11-20', 'Требуется ремонт', 1, 'admin'),
(9, '2024-11-25', 'Исправен', 4, 'admin'),
(10, '2024-12-01', 'Исправен', 8, 'admin');

-- Заполнение trips (10 поездок)
INSERT INTO scheme1.trips ("driverId", "carId", "fuelBefore", "fuelAfter", "startDatetime", "endDatetime", "mileageBefore", "mileageAfter", "startPoint", "endPoint", created_by) VALUES
(1, 1, 40, 25, '2024-12-01 09:00:00', '2024-12-01 11:30:00', 5000, 5250, 1, 2, 'admin'),
(2, 2, 35, 20, '2024-12-02 10:00:00', '2024-12-02 13:00:00', 10000, 10280, 2, 3, 'admin'),
(3, 3, 50, 30, '2024-12-03 08:30:00', '2024-12-03 12:00:00', 8000, 8320, 3, 4, 'admin'),
(4, 4, 45, 28, '2024-12-04 11:00:00', '2024-12-04 14:00:00', 15000, 15250, 4, 5, 'admin'),
(5, 5, 55, 40, '2024-12-05 09:30:00', '2024-12-05 12:30:00', 3000, 3220, 5, 6, 'admin'),
(6, 6, 30, 15, '2024-12-06 13:00:00', '2024-12-06 15:30:00', 12000, 12280, 6, 7, 'admin'),
(7, 7, 38, 22, '2024-12-07 08:00:00', '2024-12-07 11:00:00', 20000, 20250, 7, 8, 'admin'),
(8, 8, 42, 25, '2024-12-08 10:30:00', '2024-12-08 13:30:00', 7000, 7280, 8, 9, 'admin'),
(9, 9, 48, 32, '2024-12-09 09:00:00', '2024-12-09 12:00:00', 11000, 11250, 9, 10, 'admin'),
(10, 10, 35, 20, '2024-12-10 14:00:00', '2024-12-10 16:00:00', 2500, 2750, 1, 10, 'admin');