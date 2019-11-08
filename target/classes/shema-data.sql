CREATE TABLE if not exists `designation` (
                               `dsgn_id` int(11) NOT NULL AUTO_INCREMENT,
                               `lvl_id` float NOT NULL,
                               `role` varchar(255) DEFAULT NULL,
                               PRIMARY KEY (`dsgn_id`),
                               UNIQUE KEY `role_UNIQUE` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `employee` (
                            `emp_id` int(11) NOT NULL AUTO_INCREMENT,
                            `emp_name` varchar(255) DEFAULT NULL,
                            `manager_id` int(11) DEFAULT NULL,
                            `dsgn_id` int(11) DEFAULT NULL,
                            PRIMARY KEY (`emp_id`),
                            KEY `FKovlylondxh39kkd0rhdd15t8o3` (`dsgn_id`),
                            CONSTRAINT `FKovlylondxh39kkd0rhdd15t8o3` FOREIGN KEY (`dsgn_id`) REFERENCES `designation` (`dsgn_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ;
