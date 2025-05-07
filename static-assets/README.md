# Static Assets management

To avoid the cost associated with image hosting (and/or traffic), and stay in the `low tech` vibe of this project, we've decided to use `GitHub` pages to store static assets.

This is done through the `GitHub` action [`deploy-static-assets.yml`](../.github/workflows/deploy-static-assets.yml).

This action simply deploys this directory as a static website (after suppression of this `README` file) to the domain configured in the settings of this repo.

The mapping is based on a `convention`: each `Entity type` has its own directory in the [`img`](./img) directory. Each `Entity` will be associated with the image which name is the `id` of this `Entity`:

an `Event` with `id` 123 will be mapped to the `image` at `img/events/123.png`.

The `GitHub` action is configured to run each time this `static-assets` directory is updated on the `main` branch of this repo (excluding this file).

As everything is built on a convention, we will have to exclusively use `png` images.

The [`index.html`](./index.html) is just a short presentation of this project, with two links :
- to the app itself
- to the Devoxx4Kids Québec page on the devoxx4kids.org

## How to associate an Image with an Event or an Activity

1. Create an `Event` in the app, as an `admin` with the dedicated form
2. After the creation, in the `event-list` view, find the conventional `image path` in light gray under the `Event` date
3. Create a `PR` to add the expected `image` in this `static-assets` directory at the designated path
4. Wait for the deployment to happen, your image will eventually be available

You can create as many images as you want in one `PR`.

The same logic can be followed for `Activity`, the conventional `image path` is in light gray under the `Activity` title.

## How to deal with missing images (not defined or waiting for a deployment to happen)

There is a fallback image at [`default.png`](./img/event/default.png). This is the robot head from the Devoxx4Kids logo.

The easiest way to deal with fallback images in pure `html` is to use the `object` tag:

```html
<object
    data="https://montrealjug.github.io/billetterie/img/event/123.png"
    class="object-cover object-center size-full rounded-lg"
    type="image/png"
>
  <img src="https://montrealjug.github.io/billetterie/img/event/default.png"
       alt="event default image"
       class="object-cover object-center size-full rounded-lg"
  />
</object>
```

The `object` tag is [wildly supported](https://caniuse.com/mdn-html_elements_object) and in older browser, users will only see the fallback images.

To ease the usage and avoid repetition, a `jte template` is available: [`image_with_fallback.jte`](../src/main/jte/layouts/image_with_fallback.jte).

To call it in any template, use:
```jte
@template.layouts.image_with_fallback(
    entityType = "event",
    id = event.id(),
    cssClass = "object-cover object-center size-full rounded-lg"
)
```

This snippet comes from [`index.jte`](../src/main/jte/index.jte).
