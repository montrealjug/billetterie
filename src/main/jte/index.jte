@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import org.montrealjug.billetterie.ui.Utils

@param PresentationEvent event = null
@param String error = null

@template.layouts.guest_layout(
title = "Event List",
content = @`
<div class="flex flex-col items-center justify-center-safe h-full w-full bg-gray-100 py-2 md:p-2 lg:p-4">
    @if(error != null)
        <div id="main-error-message" class="bg-red-500 text-white p-4 mb-4 rounded-md w-full max-w-4xl">
            ${error}
        </div>
    @endif
    @if(event != null)
    <div class="w-full max-w-full h-full md:rounded md:h-auto md:max-h-full md:p-4 lg:p-8 lg:max-w-6xl flex flex-col justify-end-safe md:bg-white md:shadow-md">
        <div class="bg-gray-100 p-2 grow-1 w-full max-w-full md:rounded-md md:shadow md:p-4 overflow-y-auto flex flex-col">
            <div class="flex flex-col md:flex-row-reverse items-start justify-between w-full">
                <div class="w-full h-24 md:w-1/2">
                    @template.layouts.image_with_fallback(
                        entityType = "event",
                        cssClass = "object-contain size-full rounded-lg",
                        id = event.id()
                    )
                </div>
                <div class="md:w-1/2">
                    <h2 class="text-lg font-semibold text-gray-700">${event.title()}</h2>
                    <p class="mt-1">${Utils.DATE_FORMAT.format(event.date())}</p>
                    <p class="mt-1 text-sm md:text-base">
                        <span class="font-semibold text-gray-700">Lieu / Location:&nbsp;</span>
                        <span>${event.location()}</span>
                    </p>
                </div>
            </div>
            <div class="my-2">$unsafe{event.description()}</div>
            <!-- Activities Table -->
            <h3 class="text-md font-semibold text-gray-700 md:ml-0">Activities</h3>
            <div class="bg-white rounded md:rounded-md md:shadow">
            @for(PresentationActivity activity: event.activities())
                <article class="my-4 flex flex-row items-start justify-between md:my-8">
                    <div class="size-12 shrink-0 mr-2 md:size-16 md:mr-4">
                        @template.layouts.image_with_fallback(
                            entityType = "activity",
                            cssClass = "object-cover object-center size-full rounded-lg",
                            id = activity.id()
                        )
                    </div>
                    <div class="grow-1 flex flex-col justify-between">
                        <h4>${activity.title()}</h4>
                        <details name="activity" class="mt-2">
                            <summary class="italic text-gray-700 list-none cursor-pointer">details</summary>
                            <div class="mt-2">$unsafe{activity.description()}</div>
                        </details>
                    </div>
                    <div class="flex flex-col justify-between items-end mx-2 self-stretch md:mx-4">
                        <time>${Utils.TIME_FORMAT.format(activity.time())}</time>
                        <div class="flex flex-row items-center">
                            @if (activity.waitingParticipants().isEmpty())
                            <span class="text-left text-sm text-gray-700">seats:</span><span class="ml-2 w-12 text-right">${activity.participants().size()}/${activity.maxParticipants()}</span>
                            @else
                            <span class="text-left text-sm text-gray-700">waiting list:</span><span class="ml-2 w-12 text-right">${activity.waitingParticipants().size()}/${activity.maxWaitingQueue()}</span>
                            @endif
                        </div>
                    </div>
                </article>
            @endfor
            </div>
        </div>
        <div class="flex justify-end pt-2 pr-2 md:pr-0 md:pt-4 lg:pt-8">
            <button id="register-button" onclick="displayRegistrationModal()"
                    class="bg-d4k-orange text-white px-3 py-2 rounded border hover:opacity-80 focus:outline-none focus:ring focus:ring-orange-300">
                Register
            </button>
        </div>
        <div id="success-registration-message" class="md:mt-4 p-4 bg-green-100 text-green-700 rounded-md hidden">
            You have successfully registered your email address, please check your inbox and click on the verification link to register your kids to the activities
        </div>
        <div id="success-returning-message" class="md:mt-4 p-4 bg-green-100 text-green-700 rounded-md hidden">
            We've sent you an email to finish the registration, please check your inbox and click on the registration link to register your kids to the activities
        </div>
    </div>
    @else
    <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
        There is no event planned currently
    </div>
    @endif
</div>
<script>
    function displayRegistrationModal() {
        // Create the modal elements
        const modal = document.createElement("div");
        modal.id = "registration-modal";
        modal.className = "fixed inset-0 flex items-center justify-center bg-black/50 z-50";
        @raw
        modal.innerHTML = `
        <div class="bg-white rounded-lg shadow-md p-6 w-96">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Register</h2>
            <p id="first-registration-message" class="text-gray-800 text-sm mb-4 hidden">
              You're registering for the first time, please let us know a little more about yourself
            </p>
            <form id="registration-form" class="space-y-4">
                <fieldset>
                    <div>
                        <label for="email" class="block text-gray-700">Email</label>
                        <input type="email" id="email" name="email" class="border border-gray-300 rounded px-3 py-2 w-full" required>
                    </div>
                </fieldset>
                <p id="error-message" class="text-red-500 text-sm hidden"></p>
                <div class="flex justify-end gap-2">
                    <button type="button" onclick="closeModal()" class="bg-gray-300 text-gray-700 px-3 py-1 rounded hover:bg-gray-400 focus:outline-none focus:ring">Cancel</button>
                    <button type="submit" class="bg-d4k-orange text-white px-3 py-1 rounded hover:opacity-80 focus:outline-none focus:ring focus:ring-orange-300">Submit</button>
                </div>
            </form>
        </div>
        `;
        @endraw
        // add the modal in DOM
        document.body.appendChild(modal);
        const form = modal.querySelector("#registration-form");
        form.addEventListener("submit", onRegistrationFormSubmit);
    }

    function closeModal() {
        const modal = document.getElementById("registration-modal");
        if (modal) {
            modal.remove();
        }
    }

    function displayFullRegistrationForm(form, email) {
        const fieldSet = form.querySelector("fieldset");
        @raw
        fieldSet.innerHTML = `
        <div>
            <label for="first-name" class="block text-gray-700">First Name</label>
            <input type="text" id="first-name" name="firstName" class="border border-gray-300 rounded px-3 py-2 w-full" required>
        </div>
        <div>
            <label for="last-name" class="block text-gray-700">Last Name</label>
            <input type="text" id="last-name" name="lastName" class="border border-gray-300 rounded px-3 py-2 w-full" required>
        </div>
        <div>
            <label for="email" class="block text-gray-700">Email</label>
            <input type="email" id="email" name="email" class="border border-gray-300 rounded px-3 py-2 w-full" required>
        </div>
        <div>
            <label for="confirm-email" class="block text-gray-700">Confirm Email</label>
            <input type="email" id="confirm-email" name="confirmEmail" class="border border-gray-300 rounded px-3 py-2 w-full" required>
        </div>
        `;
        @endraw
        form.email.value = email;
        form.parentElement.querySelector("#first-registration-message").classList.remove("hidden");
    }
    async function onRegistrationFormSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const errorMessage = form.querySelector("#error-message");
        errorMessage.classList.add("hidden");
        const isFullRegistration = form.querySelector("#confirm-email") !== null;
        if (isFullRegistration) {
            await registerNewBooker(form, errorMessage);
        } else {
            await checkReturningBooker(form, errorMessage);
        }
    }

    async function checkReturningBooker(form, errorMessage) {
        const email = form.email.value.trim();
        if (!email) {
            errorMessage.textContent = "Please provide an email address.";
            errorMessage.classList.remove("hidden");
            return;
        }
        try {
            const response = await fetch("/check-returning-booker", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email }),
            });
            switch (response.status) {
                case 204:
                    onRegistrationCompleted("returning");
                    break;
                case 404:
                    displayFullRegistrationForm(form, email);
                    break;
                default:
                    console.error("unexpected status code: " + response.status);
                    errorMessage.textContent = "An error occurred. Please try again.";
                    errorMessage.classList.remove("hidden");
                    break;
            }
        } catch (error) {
            console.error("Error during registration:", error);
            errorMessage.textContent = "An error occurred. Please try again.";
            errorMessage.classList.remove("hidden");
        }
    }

    async function registerNewBooker(form, errorMessage) {
        // Gather form data
        const firstName = form.firstName.value.trim();
        const lastName = form.lastName.value.trim();
        const email = form.email.value.trim();
        const confirmEmail = form.confirmEmail.value.trim();
        // Validate inputs
        if (!firstName || !lastName || !email || !confirmEmail) {
            errorMessage.textContent = "Please fill in all fields.";
            errorMessage.classList.remove("hidden");
            return;
        }
        if (email !== confirmEmail) {
            errorMessage.textContent = "Emails do not match.";
            errorMessage.classList.remove("hidden");
            return;
        }
        // Send HTTP POST request
        try {
            const response = await fetch("/register-booker", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ firstName, lastName, email }),
            });
            if (response.ok) {
                onRegistrationCompleted("registration");
            } else {
                const errorData = await response.json();
                errorMessage.textContent = errorData.message || "Registration failed.";
                errorMessage.classList.remove("hidden");
            }
        } catch (error) {
            console.error("Error during registration:", error);
            errorMessage.textContent = "An error occurred. Please try again.";
            errorMessage.classList.remove("hidden");
        }
    }

    function onRegistrationCompleted(registrationType) {
        closeModal();
        @raw
        const successMessageId = `success-${registrationType}-message`;
        @endraw
        // Show success message
        const successMessage = document.getElementById(successMessageId);
        successMessage.classList.remove("hidden");
        // Disable register button
        const registerButton = document.getElementById("register-button");
        registerButton.disabled = true;
        registerButton.classList.add("hidden", "md:inline", "md:opacity-50", "md:cursor-not-allowed");
    }
</script>
`)
