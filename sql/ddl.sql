create table authority (
                           authority_name varchar(50) not null,
                           primary key (authority_name)
) engine=INNODB;

create table post (
                      post_id bigint not null auto_increment,
                      created_date datetime,
                      modified_date datetime,
                      content varchar(500) not null,
                      title varchar(100) not null,
                      user_id bigint,
                      primary key (post_id)
) engine=INNODB;

create table user (
                      user_id bigint not null auto_increment,
                      created_date datetime,
                      modified_date datetime,
                      email varchar(100) not null,
                      nickname varchar(50) not null,
                      password varchar(100) not null,
                      primary key (user_id)
) engine=INNODB;

create table user_authority (
                                user_id bigint not null,
                                authority_name varchar(50) not null,
                                primary key (user_id, authority_name)
) engine=INNODB;