UPDATE item
SET note = (
    SELECT ix_content
    FROM itemcontent
    WHERE itemcontent.id_item = item.id
)
WHERE EXISTS (
    SELECT 1
    FROM itemcontent
    WHERE itemcontent.id_item = item.id
);