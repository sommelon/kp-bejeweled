-- DROP TABLE public.comment;

CREATE TABLE public.comment
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 99999 CACHE 1 ),
    game character varying COLLATE pg_catalog."default",
    player character varying COLLATE pg_catalog."default",
    comment character varying COLLATE pg_catalog."default",
    commentedon timestamp without time zone,
    CONSTRAINT comment_pkey PRIMARY KEY (id)
);

-- DROP TABLE public.rating;

CREATE TABLE public.rating
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 99999 CACHE 1 ),
    game character varying COLLATE pg_catalog."default",
    player character varying COLLATE pg_catalog."default",
    rating integer,
    ratedon date,
    CONSTRAINT ratings_pkey PRIMARY KEY (id)
);

-- DROP TABLE public.score;

CREATE TABLE public.score
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 99999 CACHE 1 ),
    game character varying COLLATE pg_catalog."default",
    player character varying COLLATE pg_catalog."default",
    points integer,
    playedon date,
    CONSTRAINT score_pkey PRIMARY KEY (id)
);


-- DROP TABLE public."player";

CREATE TABLE public."player"
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 999999 CACHE 1 ),
    username character varying COLLATE pg_catalog."default",
    passwd character varying COLLATE pg_catalog."default",
    CONSTRAINT player_pkey PRIMARY KEY (id)
);
