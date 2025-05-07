@template.layouts.guest_layout(title = "Admin Login", content = @`
  <div class="flex items-center justify-center">
    <form class="max-w-md w-full rounded bg-white p-8 shadow-md space-y-8" action="/admin/login" method="POST">
      <fieldset class="space-y-8 bg-gray-100 p-4 rounded-md shadow">
        <h1 class="text-center text-3xl font-bold">Sign in to access Admin</h1>
        <label for="username" class="hidden">Username:</label>
        <input type="text"
               name="username"
               required
               id="username"
               class="rounded-md w-full px-3 py-2 border border-gray-400 bg-white"
               placeholder="Username">
        <label for="password" class="hidden">Password:</label>
        <input type="password"
               name="password"
               required
               id="password"
               class="rounded-md w-full px-3 py-2 border border-gray-400 bg-white"
               placeholder="Password">
      </fieldset>
      <div class="flex justify-between">
        <button type="submit"
                class="w-36 text-white bg-d4k-orange px-3 py-2 rounded hover:bg-orange-600 focus:outline-none focus:ring focus:ring-orange-600">
          Login
        </button>
        <button type="button"
                onclick="window.location.href = '/';"
                class="w-36 text-white bg-red-500 hover:bg-red-600 px-3 py-2 rounded focus:outline-none focus:ring focus:ring-red-300">
          Cancel
        </button>
      </div>
    </form>
  </div>
`)
