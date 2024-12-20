﻿DROP TABLE IF EXISTS `arch_b`;

create table arch_b(
arch_no int(11) not null AUTO_INCREMENT,
arch_name varchar(200),
dept_id int(11),
crter varchar(200),
poolarea varchar(40),
primary key (arch_no)
);

insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('1','数据1','3','3','430900');
insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('2','数据2','4','6','430900');
insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('3','数据3','5','9','430600');
insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('4','测试4','7','13','430600');
insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('5','测试5','8','17','430600');

insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('6','测试6','3','4','430700');
insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('7','测试7','4','6','430700');
insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('8','测试8','5','10','430700');
insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('9','测试9','7','13','430700');
insert into arch_b(arch_no,arch_name,dept_id,crter,poolarea) values('10','测试10','8','17','430700');


DROP TABLE IF EXISTS `sys_user`;
create table sys_user(
user_id int(11) not null AUTO_INCREMENT,
user_name varchar(200),
py varchar(100),
dept_id int(11),
primary key (user_id)
);

insert into sys_user(user_id,user_name,py,dept_id) values('1','毕淑儒','BSR','1');
insert into sys_user(user_id,user_name,py,dept_id) values('2','蔡兴熙','CXX','3');
insert into sys_user(user_id,user_name,py,dept_id) values('3','曾三杰','ZSJ','3');
insert into sys_user(user_id,user_name,py,dept_id) values('4','常元琴','CYQ','3');
insert into sys_user(user_id,user_name,py,dept_id) values('5','陈栋芬','CDF','4');
insert into sys_user(user_id,user_name,py,dept_id) values('6','陈宁婷','CNZ','4');
insert into sys_user(user_id,user_name,py,dept_id) values('7','陈瑞','CR','4');
insert into sys_user(user_id,user_name,py,dept_id) values('8','陈武宵','CWX','5');
insert into sys_user(user_id,user_name,py,dept_id) values('9','陈晓丽','CXL','5');
insert into sys_user(user_id,user_name,py,dept_id) values('10','陈翼涛','CYT','5');
insert into sys_user(user_id,user_name,py,dept_id) values('11','陈宇然','CYR','5');
insert into sys_user(user_id,user_name,py,dept_id) values('12','陈震','CZ','7');
insert into sys_user(user_id,user_name,py,dept_id) values('13','程太君','CTJ','7');
insert into sys_user(user_id,user_name,py,dept_id) values('14','程玉娜','CYN','7');
insert into sys_user(user_id,user_name,py,dept_id) values('15','丛贺轩','CHX','7');
insert into sys_user(user_id,user_name,py,dept_id) values('16','戴国宇','DGY','8');
insert into sys_user(user_id,user_name,py,dept_id) values('17','戴杰亮','DJL','8');
insert into sys_user(user_id,user_name,py,dept_id) values('18','丁玉华','DYH','8');
insert into sys_user(user_id,user_name,py,dept_id) values('19','董秋明','DQM','8');
insert into sys_user(user_id,user_name,py,dept_id) values('20','董政厚','DZH','8');


DROP TABLE IF EXISTS `sys_dept`;
create table sys_dept(
dept_id int(11) not null AUTO_INCREMENT,
dept_name varchar(200),
dept_up_id int(11) not null,
primary key (dept_id)
);

insert into sys_dept(dept_id,dept_name,dept_up_id) values('1','长沙本部','0');
insert into sys_dept(dept_id,dept_name,dept_up_id) values('2','研发部','1');
insert into sys_dept(dept_id,dept_name,dept_up_id) values('3','研发一部','2');
insert into sys_dept(dept_id,dept_name,dept_up_id) values('4','研发二部','2');
insert into sys_dept(dept_id,dept_name,dept_up_id) values('5','研发三部','2');

insert into sys_dept(dept_id,dept_name,dept_up_id) values('6','测试部','1');
insert into sys_dept(dept_id,dept_name,dept_up_id) values('7','测试一部','6');
insert into sys_dept(dept_id,dept_name,dept_up_id) values('8','测试二部','6');

DROP TABLE IF EXISTS `sys_role`;
create table sys_role(
role_id int(11) not null AUTO_INCREMENT,
role_name varchar(200),
primary key (role_id)
);
insert into sys_role(role_id,role_name) values('1','员工');
insert into sys_role(role_id,role_name) values('2','部门经理');
insert into sys_role(role_id,role_name) values('3','中心经理');



DROP TABLE IF EXISTS `sys_role_user`;
create table sys_role_user(
id int(11) not null auto_increment,
role_id int(11),
user_id int(11),
primary key (id)
);

insert into sys_role_user(id,role_id,user_id) values('1','3','1');
insert into sys_role_user(id,role_id,user_id) values('2','2','2');
insert into sys_role_user(id,role_id,user_id) values('3','2','3');
insert into sys_role_user(id,role_id,user_id) values('4','1','4');
insert into sys_role_user(id,role_id,user_id) values('5','2','5');
insert into sys_role_user(id,role_id,user_id) values('6','1','6');
insert into sys_role_user(id,role_id,user_id) values('7','1','7');

insert into sys_role_user(id,role_id,user_id) values('8','2','5');
insert into sys_role_user(id,role_id,user_id) values('9','1','6');
insert into sys_role_user(id,role_id,user_id) values('10','1','7');
insert into sys_role_user(id,role_id,user_id) values('11','1','5');
insert into sys_role_user(id,role_id,user_id) values('12','2','6');
insert into sys_role_user(id,role_id,user_id) values('13','1','7');
insert into sys_role_user(id,role_id,user_id) values('14','1','5');
insert into sys_role_user(id,role_id,user_id) values('15','1','6');
insert into sys_role_user(id,role_id,user_id) values('16','2','7');
insert into sys_role_user(id,role_id,user_id) values('17','1','5');
insert into sys_role_user(id,role_id,user_id) values('18','1','6');
insert into sys_role_user(id,role_id,user_id) values('19','1','7');
insert into sys_role_user(id,role_id,user_id) values('20','1','7');



DROP TABLE IF EXISTS `sys_role_dept`;
create table sys_role_dept(
id int(11) not null auto_increment,
role_id int(11),
dept_id int(11),
primary key (id)
);
insert into sys_role_dept(id,role_id,dept_id) values('1','3','1');
insert into sys_role_dept(id,role_id,dept_id) values('2','3','2');
insert into sys_role_dept(id,role_id,dept_id) values('3','3','3');
insert into sys_role_dept(id,role_id,dept_id) values('4','3','4');
insert into sys_role_dept(id,role_id,dept_id) values('5','3','5');
insert into sys_role_dept(id,role_id,dept_id) values('6','3','6');
insert into sys_role_dept(id,role_id,dept_id) values('7','3','7');
insert into sys_role_dept(id,role_id,dept_id) values('8','3','8');
insert into sys_role_dept(id,role_id,dept_id) values('9','2','3');
insert into sys_role_dept(id,role_id,dept_id) values('10','3','7');

