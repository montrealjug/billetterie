<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Login</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<main>
  <div class="min-h-screen flex items-center justify-center">
    <div class="max-w-md w-full space-y-8">
      <div>
        <h1 class="text-center text-3xl font-bold">Sign in to access Admin</h1>
      </div>

      <form class="mt-8 space-y-6" action="/admin/login" method="POST">
        <div>
          <label for="username" class="hidden">Username:</label>
          <input type="text"
                 name="username"
                 required
                 id="username"
                 class="appearance-none rounded-md relative block w-full px-3 py-2 border"
                 placeholder="Username">
        </div>
        <div>
          <label for="password" class="hidden">Password:</label>
          <input type="password"
                 name="password"
                 required
                 id="password"
                 class="appearance-none rounded-md relative block w-full px-3 py-2 border"
                 placeholder="Password">
        </div>
        <div class="flex justify-between">
          <button type="submit"
                  class="group relative w-48 flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700">
            Login
          </button>
          <button type="button"
                  formaction="/"
                  class="group relative w-48 flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-red-600 hover:bg-red-700">
            Cancel
          </button>
        </div>
      </form>
    </div>
  </div>
</main>
</body>
</html>
