create table customers (
    total_points integer not null,
    id bigint not null auto_increment,
    phone_number varchar(255),
    primary key (id)
) engine = InnoDB;

create table customer_transaction_transaction_products (
    transaction_products_id bigint not null,
    customer_transaction_transaction_id varchar(255) not null
) engine = InnoDB;

create table orders (
    money_spent float(23) not null,
    order_date date,
    points_earned integer not null,
    points_spent integer not null,
    customer_id bigint,
    id bigint not null auto_increment,
    primary key (id)
) engine = InnoDB;

create table orders_ordered_products (
    order_id bigint not null,
    ordered_products_id bigint not null
) engine = InnoDB;

create table point_entries_redeemed_from (
    order_id bigint not null,
    points_entry_id bigint not null
) engine = InnoDB;

create table points_entries (
    expired bit not null,
    expiry_date date,
    num_of_points integer not null,
    customer_id bigint,
    id bigint not null auto_increment,
    primary key (id)
) engine = InnoDB;

create table products (
    points_value integer not null,
    price float(23) not null,
    id bigint not null auto_increment,
    name varchar(255),
    primary key (id)
) engine = InnoDB;

create table refund (
    money_refunded float(23) not null,
    points_refunded integer not null,
    refund_date date,
    customer_id bigint,
    id bigint not null auto_increment,
    order_id bigint,
    primary key (id)
) engine = InnoDB;

create table refund_products_refunded (
    products_refunded_id bigint not null,
    refund_id bigint not null
) engine = InnoDB;

create table role (
    id bigint not null auto_increment,
    name varchar(255),
    primary key (id)
) engine = InnoDB;

create table transaction_product_seq (next_val bigint) engine = InnoDB;

insert into transaction_product_seq values (1);

create table transaction_product (
    quantity integer not null,
    refunded_quantity integer,
    id bigint not null,
    product_id bigint,
    dtype varchar(31) not null,
    primary key (id)
) engine = InnoDB;

create table users (
    id bigint not null,
    email varchar(255),
    name varchar(255),
    password varchar(255),
    primary key (id)
) engine = InnoDB;

create table users_roles (
    role_id bigint not null,
    user_id bigint not null
) engine = InnoDB;

create table users_seq (next_val bigint) engine = InnoDB;

insert into users_seq values (1);

alter table customers
add constraint UK_6v6x92wb400iwh6unf5rwiim4 unique (phone_number);

alter table customer_transaction_transaction_products
add constraint UK_odcmee2hm0q7h0hsmqj2jdcd unique (transaction_products_id);

alter table orders_ordered_products
add constraint UK_h206lxsie4mr5t85iit4os34g unique (ordered_products_id);

alter table refund_products_refunded
add constraint UK_ss2ir3cnpvre7jdwe9hjph1n1 unique (products_refunded_id);

alter table transaction_product
add constraint UK_jkxoaby9h2vt0rvi5igv45k9x unique (product_id);

alter table users
add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);

alter table customer_transaction_transaction_products
add constraint FKpiw8taq2nthrvr2y1c76ace5c foreign key (transaction_products_id) references transaction_product (id);

alter table orders
add constraint FKpxtb8awmi0dk6smoh2vp1litg foreign key (customer_id) references customers (id);

alter table orders_ordered_products
add constraint FKos5ooh1jcvragm5j5g1b4ny5l foreign key (ordered_products_id) references transaction_product (id);

alter table orders_ordered_products
add constraint FKemhx9u25v1efmwpfrqwi43obq foreign key (order_id) references orders (id);

alter table point_entries_redeemed_from
add constraint FK7r1p8loevu22rm7esjqa3r028 foreign key (points_entry_id) references points_entries (id);

alter table point_entries_redeemed_from
add constraint FK1a21m59s1afw0kvqgotq1qxxv foreign key (order_id) references orders (id);

alter table points_entries
add constraint FKcc1sxxrh25nkss7gaj8kr773s foreign key (customer_id) references customers (id);

alter table refund
add constraint FKccb5llyotysli10gu3d2vckjt foreign key (customer_id) references customers (id);

alter table refund
add constraint FK80vls36avhp4yl7h8apkqm0ek foreign key (order_id) references orders (id);

alter table refund_products_refunded
add constraint FK1jua2bbf2dmd805c14plhsty9 foreign key (products_refunded_id) references transaction_product (id);

alter table refund_products_refunded
add constraint FKndbkwgre7ya7lhi2usev6xsm0 foreign key (refund_id) references refund (id);

alter table transaction_product
add constraint FK3m1m56odw52i4cx35k9kjt0kd foreign key (product_id) references products (id);

alter table users_roles
add constraint FKt4v0rrweyk393bdgt107vdx0x foreign key (role_id) references role (id);

alter table users_roles
add constraint FK2o0jvgh89lemvvo17cbqvdxaa foreign key (user_id) references users (id);