alter table comment
drop
foreign key FKs1slvnkuemjsq2kj4h3vhx7i1;

alter table comment
drop
foreign key FK8kcum44fvpupyw6f5baccx25c;

alter table post
drop
foreign key FK59u555l1rdj2adjmxswcsni84;

alter table post
drop
foreign key FK72mt33dhhs48hf9gcqrq4fxte;

alter table post_like
drop
foreign key FKj7iy0k7n3d0vkh8o7ibjna884;

alter table post_like
drop
foreign key FKhuh7nn7libqf645su27ytx21m;

alter table user_authority
drop
foreign key FK6ktglpl5mjosa283rvken2py5;

alter table user_authority
drop
foreign key FKpqlsjpkybgos9w2svcri7j8xy;

drop table if exists authority;

drop table if exists comment;

drop table if exists post;

drop table if exists post_category;

drop table if exists post_like;

drop table if exists user;

drop table if exists user_authority;

create table authority
(
    authority_name varchar(50) not null,
    primary key (authority_name)
) engine=INNODB;

create table comment
(
    comment_id    bigint        not null auto_increment,
    created_date  datetime,
    modified_date datetime,
    content       varchar(5000) not null,
    post_id       bigint,
    user_id       bigint,
    primary key (comment_id)
) engine=INNODB;

create table post
(
    post_id          bigint       not null auto_increment,
    created_date     datetime,
    modified_date    datetime,
    content          TEXT         not null,
    title            varchar(500) not null,
    view             bigint,
    post_category_id bigint,
    user_id          bigint,
    primary key (post_id)
) engine=INNODB;

create table post_category
(
    post_category_id bigint not null auto_increment,
    category_name    varchar(255),
    primary key (post_category_id)
) engine=INNODB;

create table post_like
(
    post_like_id bigint not null auto_increment,
    post_id      bigint,
    user_id      bigint,
    primary key (post_like_id)
) engine=INNODB;

create table user
(
    user_id       bigint       not null auto_increment,
    created_date  datetime,
    modified_date datetime,
    email         varchar(100) not null,
    nickname      varchar(20)  not null,
    password      varchar(100) not null,
    primary key (user_id)
) engine=INNODB;

create table user_authority
(
    user_id        bigint      not null,
    authority_name varchar(50) not null,
    primary key (user_id, authority_name)
) engine=INNODB;

alter table user
    add constraint UK_ob8kqyqqgmefl0aco34akdtpe unique (email);

alter table comment
    add constraint FKs1slvnkuemjsq2kj4h3vhx7i1
        foreign key (post_id)
            references post (post_id);

alter table comment
    add constraint FK8kcum44fvpupyw6f5baccx25c
        foreign key (user_id)
            references user (user_id);

alter table post
    add constraint FK59u555l1rdj2adjmxswcsni84
        foreign key (post_category_id)
            references post_category (post_category_id);

alter table post
    add constraint FK72mt33dhhs48hf9gcqrq4fxte
        foreign key (user_id)
            references user (user_id);

alter table post_like
    add constraint FKj7iy0k7n3d0vkh8o7ibjna884
        foreign key (post_id)
            references post (post_id);

alter table post_like
    add constraint FKhuh7nn7libqf645su27ytx21m
        foreign key (user_id)
            references user (user_id);

alter table user_authority
    add constraint FK6ktglpl5mjosa283rvken2py5
        foreign key (authority_name)
            references authority (authority_name);

alter table user_authority
    add constraint FKpqlsjpkybgos9w2svcri7j8xy
        foreign key (user_id)
            references user (user_id);