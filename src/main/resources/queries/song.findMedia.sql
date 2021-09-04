SELECT s.id,
       (:baseUrl || FIRST_VALUE(r.uri) OVER (PARTITION BY s.id)) AS url
FROM song s
         LEFT JOIN resource_info r ON s.id = r.media_id AND r.storage_type = :storageType
WHERE s.id = :songId