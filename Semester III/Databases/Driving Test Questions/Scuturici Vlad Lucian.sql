--Scuturici Vlad Lucian - grupa 30225
--Subiect 8
--08.01)
--a,b,c,d,e,f,g)
--Se creeaza tabelele

CREATE TABLE Intrebare (
    id_i int NOT NULL,
    intrebare varchar(255) NOT NULL,
    poza varchar(255),
    sectiune varchar(255),
    categorie varchar(255),
    PRIMARY KEY (id_i)
);

--

CREATE TABLE Raspuns (
    id_i int NOT NULL,
    id_r varchar(1),
    raspuns varchar(255),
    corect varchar(1),
);
ALTER TABLE Raspuns
ADD CONSTRAINT PK_Raspuns PRIMARY KEY(id_i, id_r);
--
ALTER TABLE Raspuns
ADD CONSTRAINT Raspuns_ck CHECK (id_r='a' OR id_r='b' OR id_r='c' OR id_r='d');
--
ALTER TABLE Raspuns
ADD CONSTRAINT Raspuns_ck2 CHECK (corect='D' OR corect='N');

--

CREATE TABLE Chestionar (
    id_c int NOT NULL,
    denumire varchar(20),
    punctaj_max int,
    PRIMARY KEY (id_c)
);

--

CREATE TABLE Intrebari_Chestionar (
    id_c int NOT NULL,
    id_i int NOT NULL,
    nr_ordine int,
    CONSTRAINT FK_Intrebari_Chestionar FOREIGN KEY (id_c)
    REFERENCES Chestionar(id_c)
)
--
ALTER TABLE Intrebari_Chestionar
ADD CONSTRAINT FK_Intrebari_Chestionar2
FOREIGN KEY (id_i) REFERENCES Intrebare(id_i);

--

CREATE TABLE Test (
    id_t int NOT NULL,
    data DATE,
    punctaj int,
    id_c int,
    PRIMARY KEY (id_t)
);
--
ALTER TABLE Test
ADD CONSTRAINT FK_Test FOREIGN KEY (id_c) REFERENCES Chestionar (id_c);

--

CREATE TABLE Raspuns_Test (
    id_t int NOT NULL,
    id_i int NOT NULL,
    id_r varchar(1),
    corect varchar(1),
);
--
ALTER TABLE Raspuns_test
ADD CONSTRAINT FK_Raspuns_test1 FOREIGN KEY (id_t) REFERENCES test(id_t);
--
ALTER TABLE Raspuns_test
ADD CONSTRAINT FK_Raspuns_test2 FOREIGN KEY (id_i, id_r) REFERENCES Raspuns(id_i, id_r);
--
ALTER TABLE Raspuns_test
ADD CONSTRAINT Raspuns_test_ck CHECK (corect='D' OR corect='N');

--Adaugam elemente in tabel

INSERT INTO Intrebare
VALUES (1, 'Ce obligatii aveti la semnalul galben intermitent al semaforului electric?', 'poza1', 'Semnale luminoase', 'B, B1');
--
INSERT INTO Intrebare
VALUES (2, 'Schimbarea directiei de mers la dreapta sau la stanga nu este perimisa la intalnirea:', 'poza2', 'Indicatoare si marcaje', 'B, B1');
--
INSERT INTO Intrebare
VALUES (3, 'Care indicator te avertizeaza ca urmeaza sa te intersectezi cu un drum fara prioritate?', 'poza3', 'Indicatoare si marcaje', 'B, B1');
--
INSERT INTO Intrebare
VALUES (4, 'La intalnirea panoului de pe stalp aveti obligatia: ', 'poza4', 'Indicatoare si marcaje', 'B, B1');
--
INSERT INTO Intrebare
VALUES (5, 'Indicatorul interzice accesul:', 'poza5', 'Indicatoare si marcaje', 'B, B1');
--
INSERT INTO Intrebare
VALUES (6, 'Ce semnifica indicatorul cu panoul aditional?', 'poza6', 'Indicatoare si marcaje', 'B, B1');
--
INSERT INTO Intrebare
VALUES (7, 'Este corect sa depasesti in fata ta?', 'poza7', 'Depasirea', 'B, B1');
--
INSERT INTO Intrebare
VALUES (8, 'In care situatie de mai jos este interzisa depasirea vehiculelor?', NULL, 'Depasirea', 'B, B1');
--
INSERT INTO Intrebare
VALUES (9, 'Viteza maxima cu care pot sa conduca un automobil, pe drumurile nationale altele decat europene, persoanele care au mai putin de un an de practica este de: ', NULL, 'Viteza si distanta intre vehicule', 'B, B1');
--
INSERT INTO Intrebare
VALUES (10, 'In ce ordine vor trece urmatoarele autovehicule?', 'poza10', 'Prioritate de trecere', 'B, B1');
--
INSERT INTO Raspuns
VALUES (1, 'a', 'Opriti', 'N');
--
INSERT INTO Raspuns
VALUES (1, 'b', 'Reduceti viteza si continuati deplasarea', 'N');
--
INSERT INTO Raspuns
VALUES (1, 'c', 'Reduceti viteza si respectati regulile de circulatie valabile in acea intersectie', 'D');
--
INSERT INTO Raspuns
VALUES (1, 'd', 'Continuati deplasarea', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (2, 'a', 'Indicatorul 1', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (2, 'b', 'Indicatorul 2', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (2, 'c', 'Ambele', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (2, 'd', 'Niciunul', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (3, 'a', 'Ambele', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (3, 'b', 'Indicatorul 1', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (3, 'c', 'Indicatorul 2', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (3, 'd', 'Niciunul', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (4, 'a', 'Sa va deplasati cu atentie', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (4, 'b', 'Sa acordati prioritate', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (4, 'c', 'Sa opriti', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (4, 'd', 'Sa incetiniti', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (5, 'a', 'Autoturismelor', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (5, 'b', 'Autovehiculelor si vehiculelor cu tractiune animala', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (5, 'c', 'Motocicletelor fara atas', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (5, 'd', 'Motocicletelor cu atas', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (6, 'a', 'Sfarsitul zonei "Stationare interzisa"', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (6, 'b', 'Sfarsitul zonei "Oprire interzisa"', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (6, 'c', 'Inceputul zonei "Oprire interzisa"', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (6, 'd', 'Inceputul zonei "Stationare interzisa"', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (7, 'a', 'Da"', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (7, 'b', 'Nu', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (7, 'c', 'Depinde de tipul drumului', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (7, 'd', 'Depinde de tipul vehicululi', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (8, 'a', 'Cand din sens opus se apropie alt vehicul', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (8, 'b', 'In apropierea varfurilor de panta, cand vizibilitatea este redusa sub 50m', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (8, 'c', 'In statiile de tramvai prevazute cu refugiu pentru pietoni', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (8, 'd', 'In statiile de tramvai', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (9, 'a', '70km/h', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (9, 'b', '80km/h', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (9, 'c', '100km/h', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (9, 'd', '110km/h', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (10, 'a', 'Autobuz, autocamion, autoturism', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (10, 'b', 'Autocamion, autobuz, autoturism', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (10, 'c', 'Autoturism, autobuz, autocamion', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES (10, 'd', 'Autoturism, autocamion, autobuz', 'N');
--
INSERT INTO Chestionar
VALUES (1, 'Chestionar 1', 5);
--
INSERT INTO Chestionar
VALUES (2, 'Chestionar 2', 2);
--
INSERT INTO Intrebari_Chestionar
VALUES (1, 1, 1);
--
INSERT INTO Intrebari_Chestionar
VALUES (1, 2, 2);
--
INSERT INTO Intrebari_Chestionar
VALUES (1, 6, 3);
--
INSERT INTO Intrebari_Chestionar
VALUES (1, 7, 4);
--
INSERT INTO Intrebari_Chestionar
VALUES (1, 9, 5);
--
INSERT INTO Intrebari_Chestionar
VALUES (2, 1, 1);
--
INSERT INTO Intrebari_Chestionar
VALUES (2, 2, 2);
--
INSERT INTO Test
VALUES (1, '10-Dec-2022', 2, 1);
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 1, 'a', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 1, 'b', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 1, 'c', 'D');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 1, 'd', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 2, 'a', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 2, 'b', 'D');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 2, 'c', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 2, 'd', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 6, 'a', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 6, 'b', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 6, 'c', 'D');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 6, 'd', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 7, 'a', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 7, 'b', 'D');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 7, 'c', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 7, 'd', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 9, 'a', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 9, 'b', 'D');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 9, 'c', 'N');
--
INSERT INTO Raspuns_test (id_t, id_i, id_r, corect)
VALUES (1, 9, 'd', 'N');
--h)
ALTER TABLE Intrebare
MODIFY(categorie VARCHAR2(50));

--08.02)
--a)
ALTER TABLE Intrebari_Chestionar
ADD CONSTRAINT intrebari_chestionar_ck CHECK(nr_ordine BETWEEN 1 AND 26);

--b)
ALTER TABLE Intrebare
ADD CONSTRAINT intrebare_ck CHECK(poza IS NOT NULL OR (sectiune != 'Indicatoare si marcaje' AND poza IS NULL));

--08.03)
--a)
SELECT * 
FROM Intrebari_Chestionar
WHERE id_c = 1
ORDER BY nr_ordine;

--b)
SELECT * 
FROM raspuns_test
WHERE id_t = 1
ORDER BY id_i, id_r ASC;

--08.04)
--a)
SELECT test.id_t AS "ID-ul testului", raspuns.id_i AS "ID-ul intrebarii", raspuns.id_r AS "Varianta alesa", raspuns.raspuns AS "Raspuns model", raspuns.corect AS "Corectitudine model", raspuns_test.corect AS "Rezolvare"
FROM test INNER JOIN raspuns_test ON (test.id_t = raspuns_test.id_t) JOIN raspuns ON (raspuns.id_i = raspuns_test.id_i AND raspuns.id_r = raspuns_test.id_r)
WHERE test.punctaj > 22 

--b)
SELECT DISTINCT chestionar1.id_c AS "Chestionar 1", chestionar2.id_c AS "Chestionar 2"
FROM Intrebari_Chestionar chestionar1 INNER JOIN Intrebari_Chestionar chestionar2 ON (chestionar1.id_i = chestionar2.id_i)
WHERE chestionar1.id_c < chestionar2.id_c
ORDER BY chestionar1.id_c, chestionar2.id_c;

--08.05)
--a)

--b)
SELECT id_t AS "ID-ul testului", punctaj
FROM Test
WHERE punctaj <= ALL(SELECT punctaj FROM Test);

--08.06)
--a)
SELECT id_c, denumire, punctaj_max
FROM Chestionar
WHERE punctaj_max = (SELECT MIN(punctaj_max) FROM Chestionar);

--b)

--08.07)
--a)
INSERT INTO Intrebare (id_i, intrebare, poza, sectiune, categorie)
VALUES(100, 'Care din urmatoarele fapte se sanctioneaza doar cu amenda contraventionala si retinerea certificatului de inmatriculare?', NULL, 'Sanctiuni si infractiuni', 'B, B1');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES(100, 'a', 'Conducerea unui autovehicul cu defectiuni la sistemul de directie', 'N');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES(100, 'b', 'Conducerea unui autovehicul cu defectiuni la sistemul de iluminare', 'D');
--
INSERT INTO Raspuns (id_i, id_r, raspuns, corect)
VALUES(100, 'c', 'Conducerea unui autovehicul cu elemente de caroserie lipsa', 'D');

--b)
DELETE FROM Test
WHERE id_t NOT IN (SELECT id_t FROM Raspuns_test);

--c)



