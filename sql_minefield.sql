/*** Useful maintenance queries ***/
-- select crawled, count(*) from Docs group by crawled;
-- select indexed, count(*) from Docs group by indexed;


/*** Searching multiple words ***/
/*
I will ignore this because the document requires
us to aggregate individual word scores
*/
	-- attempt 1 using nesting and having
		select link
		from Docs
		where Docs.doc_id in
		(
			select doc_id
			from Ranker_Dictionary
			where word in ('galaxi', 'samsung')
			group by doc_id
			having count(distinct word) = 2
		);

	-- attempt 2 using nesting and intersect
		select link
		from Docs
		where Docs.doc_id in
		(
			select doc_id
			from Ranker_Dictionary
			where word = 'galaxi'
			intersect
			select doc_id
			from Ranker_Dictionary
			where word = 'samsung'
		);

	-- attempt 3 using join
		SELECT link
		FROM Docs
		Join Ranker_Dictionary
		on Docs.doc_id = Ranker_Dictionary.doc_id
		WHERE word IN ('samsung', 'galaxi')
		GROUP BY link
		HAVING COUNT(DISTINCT word) = 2

	-- attempt 4 using join and intersect
		SELECT link
		FROM Docs
		Join Ranker_Dictionary
		on Docs.doc_id = Ranker_Dictionary.doc_id
		WHERE word = 'samsung'
		INTERSECT
		SELECT link
		FROM Docs
		Join Ranker_Dictionary
		on Docs.doc_id = Ranker_Dictionary.doc_id
		WHERE word = 'galaxi'



/*** Scoring for single word search ***/
	-- attempt 1
		SELECT link, count(*) as tf
		FROM Docs
		JOIN Ranker_Dictionary
		ON Docs.doc_id = Ranker_Dictionary.doc_id
		WHERE word = 'galaxi'
		GROUP BY link
		HAVING count(*) > 0
		ORDER BY count(*) DESC

	-- attempt 2 scoring tags on Tag_Scores
		WITH frequencies as	(
							SELECT doc_id, tag, count(*) as tf
							FROM Ranker_Dictionary
							WHERE word = 'cnn'
							GROUP BY doc_id, tag
							HAVING count(*) > 0
							)
		SELECT doc_id, sum(tf * score) as score
		FROM frequencies
		JOIN Tag_Scores
		ON frequencies.tag = Tag_Scores.tag
		GROUP BY doc_id
		ORDER BY score DESC;

	-- attempt 2 scoring tags on Tag_Scores
		WITH frequencies as	(
							SELECT doc_id, tag, count(*) as tf
							FROM (
									SELECT *
									FROM Ranker_Dictionary
									WHERE word = 'cnn'
									INTERSECT
									SELECT *
									FROM Ranker_Dictionary
									WHERE word = 'galaxi'
								)
								AS temp
							GROUP BY doc_id, tag
							HAVING count(*) > 0
							)
		SELECT doc_id, sum(tf * score) as score
		FROM frequencies
		JOIN Tag_Scores
		ON frequencies.tag = Tag_Scores.tag
		GROUP BY doc_id
		ORDER BY score DESC;

	-- attempt 3 scoring tags on Tag_Scores with bonus for exact match
		WITH frequencies AS
		(
			SELECT doc_id, tag, 
				SUM
				(
					CASE 
					WHEN literal_word = 'galaxy' THEN 2
					ELSE 1
					END
				) AS tf
			FROM Ranker_Dictionary
			WHERE word = 'galaxi'
			GROUP BY doc_id, tag
			HAVING COUNT(*) > 0
		)
		SELECT doc_id, SUM(tf * score) AS score
		FROM frequencies
		JOIN Tag_Scores
		ON frequencies.tag = Tag_Scores.tag
		GROUP BY doc_id
		ORDER BY score DESC;
	
	-- attempt 4 scoring on tags with case and bonus for exact match
		SELECT doc_id, SUM(
				CASE
					WHEN literal_word = 'galaxy' THEN 2
					ELSE 1
				END
				*
				CASE
					WHEN tag = 'keyword' THEN 15
					WHEN tag = 'title' THEN 8
					WHEN tag = 'h1' THEN 5
					ELSE 1
				END
			) AS score
		FROM Ranker_Dictionary
		WHERE word = 'galaxi'
		GROUP BY doc_id
		HAVING COUNT(*) > 0
		ORDER BY score DESC;

	-- attempt 5 now return links instead
		SELECT link, 
			SUM(
				CASE 
					WHEN literal_word = 'galaxy' THEN 2
					ELSE 1
				END
				*
				CASE
					WHEN tag = 'keyword' THEN 15
					WHEN tag = 'title' THEN 8
					WHEN tag = 'h1' THEN 5
					ELSE 1
				END
			) AS score
		FROM Ranker_Dictionary
		JOIN Docs
		ON Docs.doc_id = Ranker_Dictionary.doc_id
		WHERE word = 'galaxi'
		GROUP BY link
		HAVING COUNT(*) > 0
		ORDER BY score DESC;



/*** Scoring for multiple word search ***/
	-- attempt 1
		SELECT link, SUM(
				CASE 
					WHEN literal_word in ('galaxy', 'movie') THEN 2
					ELSE 1
				END
				*
				CASE
					WHEN tag = 'keyword' THEN 15
					WHEN tag = 'title' THEN 8
					WHEN tag = 'h1' THEN 5
					ELSE 1
				END
			) AS score
		FROM Ranker_Dictionary
		JOIN Docs
		ON Docs.doc_id = Ranker_Dictionary.doc_id
		WHERE word in ('galaxi', 'movi')
		GROUP BY link
		HAVING COUNT(*) > 0
		ORDER BY score DESC;

	-- attempt 2 replacing case with decode didn't work :(((
		SELECT link,
			SUM(
				DECODE(literal_word, 'galaxy', 2, 'movie', 2, 1)
				*
				DECODE(tag, 'keyword', 15, 'title', 8, 'h1', 5, 1)
			) AS score
		FROM Ranker_Dictionary
		JOIN Docs
		ON Docs.doc_id = Ranker_Dictionary.doc_id
		WHERE word in ('galaxi', 'movi')
		GROUP BY link
		HAVING COUNT(*) > 0
		ORDER BY score DESC;




/*** Searching for a phrase ***/
	-- attempt 1
		SELECT DISTINCT d.link
		FROM Docs d
		JOIN Ranker_Dictionary r1 ON d.doc_id = r1.doc_id
		JOIN Ranker_Dictionary r2 ON d.doc_id = r2.doc_id
		WHERE	r1.word = 'decis'
				AND r2.word = 'despit'
				AND r1.tag = r2.tag
				AND r1.tag_id = r2.tag_id
				AND r1.pos = r2.pos - 1
	-- attempt 2
		SELECT link, SUM(
				(
					CASE
						WHEN R1.literal_word = 'samsung' THEN 2
						ELSE 1
					END
					+
					CASE 
						WHEN R2.literal_word = 'galaxy' THEN 2
						ELSE 1
					END
				)
				*
				CASE
					WHEN R1.tag = 'keyword' THEN 15
					WHEN R1.tag = 'title' THEN 8
					WHEN R1.tag = 'h1' THEN 5
					ELSE 1
				END
		) AS score
		FROM Ranker_Dictionary R1
		JOIN Ranker_Dictionary R2
		ON	R1.word = 'samsung'
			AND R2.word = 'galaxi'
			AND R1.doc_id = R2.doc_id
			AND R1.tag = R2.tag
			AND R1.tag_id = R2.tag_id
			AND R1.pos = R2.pos - 1
		JOIN Docs
		ON Docs.doc_id = R1.doc_id
		GROUP BY link
		HAVING COUNT(*) > 0
		ORDER BY score DESC;



