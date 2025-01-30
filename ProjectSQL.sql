Use project;

create table player(
	DiscordId char(100),
	primary key(DiscordId),
    eloOverride int
);

create table role(
	RoleName char(100),
    primary key(RoleName)
);

create table account(
	RiotUsername char(255),
    RiotTagline char(255),
    primary key(RiotUsername, RiotTagline),
    link char(255),	
    accountRank double,
    DiscordId char(100),
    foreign key(DiscordId) references player(DiscordId)
);

create table team(
	TeamId int,
    primary key(TeamId),
    TeamAvgElo int,
	Player1 char(100),
	Player2 char(100),
	Player3 char(100),
	Player4 char(100),
	Player5 char(100),
    foreign key(Player1) references player(DiscordId),
    foreign key(Player2) references player(DiscordId),
    foreign key(Player3) references player(DiscordId),
    foreign key(Player4) references player(DiscordId),
    foreign key(Player5) references player(DiscordId)
);

create table matchup(
	MatchupId int,
    primary key(MatchupId),
    BlueTeam int,
    foreign key(BlueTeam) references team(TeamId),
    RedTeam int,
    foreign key(RedTeam) references team(TeamId),
	EloDist double,
    BlueWon boolean,
    date date
);


create table plays(
	EloOffset double,
    playsId int,
    primary key(playsId),
    RoleName char(100),
    foreign key(RoleName) references role(RoleName),
    DiscordId char(100),
    foreign key(DiscordId) references player(DiscordId)
);

insert into role (RoleName) Value ('Toplane');
insert into role (RoleName) Value ('Jungle');
insert into role (RoleName) Value ('Midlane');
insert into role (RoleName) Value ('Botlane');
insert into role (RoleName) Value ('Support');