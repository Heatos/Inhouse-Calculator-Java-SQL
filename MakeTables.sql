create table player(
	DiscordId char(100),
	primary key(DiscordId)
);

create table role(
	RoleName char(100),
    primary key(RoleName)
);

create table account(
	RiotId char(100),
    primary key(RiotId),
    RiotTagline char(100),
    primary key(RiotTagline),
    link char(10000),
    accountRank char(100),
    DiscordId char(100),
    foreign key(DiscordId) references player.DiscordId
);

create table plays(
	EloOffset int,
    RoleName char(100),
    foreign key(RoleName) references role.RoleName,
    DiscordId char(100),
    foreign key(DiscordId) references player.DiscordId
);

	create table playsOn(
		Player1 char(100),
		foreign key(Player1) references player.DiscordId,
		Player2 char(100),
		foreign key(Player2) references player.DiscordId,
		Player3 char(100),
		foreign key(Player3) references player.DiscordId,
		Player4 char(100),
		foreign key(Player4) references player.DiscordId,
		Player5 char(100),
		foreign key(Player5) references player.DiscordId,
		TeamId int,
		foreign key(TeamId) references team.TeamId
	);

create table team(
	TeamId int,
    primary key(TeamId),
    TeamAvgElo int
    /* Made this 5 players because there are always 5 players on a team */
);

create table PlaysVs(
	TeamBlue int,
    foreign key(TeamBlue) references team.TeamId,
    TeamRed int,
    foreign key(TeamRed) references team.TeamId,
    MatchupId int,
    foreign key(MatchupId) references matchup.MatchuId
);

create table matchup(
	MatchupId int,
    primary key(MatchupId),
	EloDist int,
    Winner char(100),
    date date
);
