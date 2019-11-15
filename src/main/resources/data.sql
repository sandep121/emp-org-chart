insert into designation (LVL_ID, ROLE) values(1,'Director');
insert into designation (LVL_ID, ROLE) values(2,'Manager');
insert into designation (LVL_ID, ROLE) values(3,'Lead');
insert into designation (LVL_ID, ROLE) values(4,'DevOps');
insert into designation (LVL_ID, ROLE) values(4,'Developer');
insert into designation (LVL_ID, ROLE) values(4,'QA');
insert into designation (LVL_ID, ROLE) values(5,'intern');

truncate table employee;
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Thor',-1 ,1);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Iron Man',1,2);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Hulk',1,3);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Captain America',1,2);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('War Machine',2,5);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Vision',2,4);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Falcon',4,6);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Ant Man',4,3);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Spider Man',2,7);
insert into employee (EMP_NAME, MANAGER_ID, DSGN_ID) values('Black Window',3,6);