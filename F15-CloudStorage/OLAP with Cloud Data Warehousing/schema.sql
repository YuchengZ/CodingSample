-- create partitioned table for all the tables 
-- partition by the query number. Partition name has a key-value
-- key is the query value is an int.

CREATE EXTERNAL TABLE part_opt (
	p_partkey INT, 
	p_name STRING, 
	p_mfgr STRING, 
	p_category STRING, 
	p_brand1 STRING, 
	p_color STRING, 
	p_type STRING, 
	p_size INT, 
	p_container STRING)
	partitioned by (query INT);


CREATE EXTERNAL TABLE supplier_opt (
	s_suppkey   INT,
	s_name STRING,
	s_address STRING,
	s_city STRING,
	s_nation STRING,
	s_region STRING,
	s_phone STRING)
	partitioned by (query INT);


CREATE EXTERNAL TABLE customer_opt(
	c_custkey INT,
	c_name STRING,
	c_address  STRING,
	c_city STRING,
	c_nation STRING,
	c_region STRING,
	c_phone STRING,
	c_mktsegment STRING)
	partitioned by (query INT);


CREATE EXTERNAL TABLE dwdate_opt(
	d_datekey INT,
	d_date STRING,
	d_dayofweek STRING,
	d_month STRING,
	d_year INT,
	d_yearmonthnum INT,
	d_yearmonth STRING,
	d_daynuminweek INT,
	d_daynuminmonth INT,
	d_daynuminyear INT,
	d_monthnuminyear INT,
	d_weeknuminyear INT,
	d_sellingseason STRING,
	d_lastdayinweekfl STRING,
	d_lastdayinmonthfl STRING,
	d_holidayfl STRING,
	d_weekdayfl STRING)
	partitioned by (query INT);


CREATE EXTERNAL TABLE lineorder_opt(
	lo_orderkey INT,
	lo_linenumber INT,
	lo_custkey INT,
	lo_partkey INT,
	lo_suppkey INT,
	lo_orderdate INT,
	lo_orderpriority STRING,
	lo_shippriority STRING,
	lo_quantity INT,
	lo_extendedprice INT,
	lo_ordertotalprice INT,
	lo_discount INT,
	lo_revenue INT,
	lo_supplycost INT,
	lo_tax INT,
	lo_commitdate INT,
	lo_shipmode STRING)
	partitioned by (query INT);

-- create partition for query 1, the key is query and value is 1 
-- (the query 11 is a new version of query1)

-- query 1 only ask for the year is 1997
insert into dwdate_opt partition (query=1) select * from dwdate where d_year=1997
-- query 1 only ask fo the discount betwwen 1 and 3 and quantity under 24. And 
-- orderdate is the same with datekey from query1.
insert into lineorder_opt partition (query=11) select 
	lo_orderkey,
	lo_linenumber,
	lo_custkey,
	lo_partkey,
	lo_suppkey,
	lo_orderdate,
	lo_orderpriority,
	lo_shippriority,
	lo_quantity,
	lo_extendedprice,
	lo_ordertotalprice,
	lo_discount,
	lo_revenue,
	lo_supplycost,
	lo_tax,
	lo_commitdate,
	lo_shipmode from lineorder, dwdate_opt 
	where dwdate_opt.query=1
	and lo_orderdate = d_datekey
	and lo_discount >= 1 
	and lo_discount <= 3 
	and lo_quantity < 24;

-- below are partition for query 2. table supplier only needs region is america

-- table part only needs category is MFGR#12
-- table lineorder nee
insert into supplier_opt partition (query=2) select * from supplier where s_region='AMERICA';
insert into part_opt partition (query=2) select * from part where p_category='MFGR#12';
insert into lineorder_opt partition (query=5) select lo_orderkey,
	lo_linenumber,
	lo_custkey,
	lo_partkey,
	lo_suppkey,
	lo_orderdate,
	lo_orderpriority,
	lo_shippriority,
	lo_quantity,
	lo_extendedprice,
	lo_ordertotalprice,
	lo_discount,
	lo_revenue,
	lo_supplycost,
	lo_tax,
	lo_commitdate,
	lo_shipmode from lineorder, part_opt, supplier_opt where lo_partkey = part_opt.p_partkey and lo_suppkey=supplier_opt.s_suppkey;

-- belows are partitions needed for query 3;
-- for table supplier and customer and dwdate, I filter the data according to the where.
-- table lineorder I filter the data by 
insert into supplier_opt partition (query=3) select * from supplier where s_city='UNITED KI1' or s_city='UNITED KI5';
insert into customer_opt partition (query=3) select * from customer where c_city='UNITED KI1' or c_city='UNITED KI5';
insert into dwdate_opt partition (query=3) select * from dwdate where d_yearmonth = 'Dec1997' ;
insert into lineorder_opt partition (query=333) select lo_orderkey,
	lo_linenumber,
	lo_custkey,
	lo_partkey,
	lo_suppkey,
	lo_orderdate,
	lo_orderpriority,
	lo_shippriority,
	lo_quantity,
	lo_extendedprice,
	lo_ordertotalprice,
	lo_discount,
	lo_revenue,
	lo_supplycost,
	lo_tax,
	lo_commitdate,
	lo_shipmode from lineorder, customer_opt, supplier_opt, dwdate_opt 
	where customer_opt.query=3
	and supplier_opt.query=3
	and dwdate_opt.query=3
	and lo_custkey = customer_opt.c_custkey 
	and lo_suppkey=supplier_opt.s_suppkey 
	and lo_orderdate=dwdate_opt.d_datekey;




