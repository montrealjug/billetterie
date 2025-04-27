@param String entityType
@param long id
@param String cssClass = ""
<object
    data="https://montrealjug.github.io/billetterie/img/${entityType}/${id}.png"
    class="${cssClass}"
    type="image/png"
>
  <img src="https://montrealjug.github.io/billetterie/img/${entityType}/default.png"
       alt="${entityType} default image"
       class="${cssClass}"
  />
</object>
