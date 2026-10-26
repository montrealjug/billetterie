@param String state
@param String signature
@param String email = null

@template.layouts.guest_layout(title = "Supprimer mon adresse courriel / Delete my email address", content = @`
  <div class="flex items-center justify-center">
    <div class="max-w-md w-full rounded bg-white p-8 shadow-md space-y-6">
      <h1 class="text-center text-2xl font-bold">Supprimer mon adresse courriel<br>Delete my email address</h1>
      @if(state.equals("CONFIRM"))
        <p lang="fr-CA">
          Voulez-vous vraiment supprimer l'adresse <strong>${email}</strong> de notre système ?
          Vous ne recevrez plus de courriels de notre part, et vous pourrez vous réinscrire à tout moment, avec cette adresse ou une autre.
        </p>
        <p lang="en-CA" class="italic text-gray-600">
          Do you really want to delete the address <strong>${email}</strong> from our system?
          You won't receive any more emails from us, and you can register again at any time, with this address or another one.
        </p>
        <form action="/bookings/${signature}/delete" method="POST" class="flex justify-between">
          <button type="submit"
                  class="text-white bg-red-500 hover:bg-red-600 px-3 py-2 rounded focus:outline-none focus:ring focus:ring-red-300">
            Supprimer / Delete
          </button>
          <a href="/"
             class="text-gray-700 bg-gray-300 hover:bg-gray-400 px-3 py-2 rounded focus:outline-none focus:ring">
            Annuler / Cancel
          </a>
        </form>
      @elseif(state.equals("DELETED"))
        <p lang="fr-CA">L'adresse <strong>${email}</strong> a été supprimée de notre système.</p>
        <p lang="en-CA" class="italic text-gray-600">The address <strong>${email}</strong> has been deleted from our system.</p>
        <p class="text-center"><a href="/" class="text-d4k-orange underline">Devoxx4Kids Québec</a></p>
      @elseif(state.equals("HAS_REGISTRATIONS"))
        <p lang="fr-CA">
          L'adresse <strong>${email}</strong> ne peut pas être supprimée, car des enfants sont (ou ont été) inscrits à nos activités avec celle-ci.
          Pour annuler des inscriptions, utilisez votre <a href="/bookings/${signature}" class="text-d4k-orange underline">lien personnel</a>.
        </p>
        <p lang="en-CA" class="italic text-gray-600">
          The address <strong>${email}</strong> can't be deleted, because children are (or were) registered to our activities with it.
          To cancel registrations, use your <a href="/bookings/${signature}" class="text-d4k-orange underline">personal link</a>.
        </p>
      @else
        <p lang="fr-CA">Cette adresse courriel n'existe pas (ou plus) dans notre système.</p>
        <p lang="en-CA" class="italic text-gray-600">This email address doesn't exist (anymore) in our system.</p>
        <p class="text-center"><a href="/" class="text-d4k-orange underline">Devoxx4Kids Québec</a></p>
      @endif
    </div>
  </div>
`)
