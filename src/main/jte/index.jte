@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import java.time.format.DateTimeFormatter

@param PresentationEvent event = null
@param String error = null

@template.layouts.guest_layout(
title = "Event List",
content = @`

<div class="flex flex-col items-center justify-center h-full">
    @if(error != null)
        <div id="main-error-message" class="bg-red-500 text-white p-4 mb-4 rounded-md w-full max-w-4xl">
            ${error}
        </div>
    @endif


    <div class="flex flex-col items-center justify-center">
        @if(event != null)
            <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">


                <div class="bg-gray-100 p-4 rounded-md shadow">
                    <div class="flex flex-col md:flex-row items-start md:items-center justify-between">
                        <div>
                            <h2 class="text-lg font-semibold text-gray-700">${event.title()}</h2>
                            <p class="text-gray-600">${event.description()}</p>
                            <p class="text-gray-500 text-sm mt-1">${event.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</p>
                        </div>
                        <div class="size-24">
                            @template.layouts.image_with_fallback(
                                entityType = "event",
                                cssClass = "object-cover object-center size-full rounded-lg",
                                id = event.id()
                            )
                        </div>
                    </div>
                    <!-- Activities Table -->
                    <div class="mt-4">
                        <h3 class="text-md font-semibold text-gray-700 mb-2">Activities</h3>
                        <table class="w-full table-auto border-collapse border border-gray-300">
                            <thead>
                            <tr class="bg-gray-200">
                                <th class="border border-gray-300 px-4 py-2 text-left">Title</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Description</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Attendance</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Waiting line</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Start Time</th>
                            </tr>
                            </thead>
                            <tbody id="activity-list-1">
                            <!-- Example Activity Row -->
                            @for(PresentationActivity activity: event.activities())
                                <tr>
                                    <td class="border border-gray-300 px-4 py-2">${activity.title()}</td>
                                    <td class="border border-gray-300 px-4 py-2">${activity.description()}</td>
                                    <td class="border border-gray-300 px-4 py-2">${activity.participants().size()}/${activity.maxParticipants()}</td>
                                    <td class="border border-gray-300 px-4 py-2">${activity.waitingParticipants().size()}/${activity.maxWaitingQueue()}</td>

                                    <td class="border border-gray-300 px-4 py-2">${activity.time().format(DateTimeFormatter.ofPattern("H:mm a"))}</td>
                                </tr>
                            @endfor

                            <!-- More rows will be dynamically added -->
                            </tbody>
                        </table>
                        <!-- Create Activity Button -->

                    </div>
                </div>

                <br>
                <div class="flex justify-end">
                    <button id="register-button" onclick="displayRegistrationModal()"
                            class="bg-d4k-orange text-white px-3 py-2 rounded border hover:opacity-80 focus:outline-none focus:ring focus:ring-orange-300">
                        Register
                    </button>
                </div>

                <div id="success-registration-message" class="mt-4 p-4 bg-green-100 text-green-700 rounded-md hidden">
                    You have successfully registered your email address, please check your inbox and click on the verification link to register your kids to the activities
                </div>
                <div id="success-returning-message" class="mt-4 p-4 bg-green-100 text-green-700 rounded-md hidden">
                    We've sent you an email to finish the registration, please check your inbox and click on the registration link to register your kids to the activities
                </div>
            </div>

        @else
            <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
                There is no event planned currently
            </div>
        @endif

    </div>

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
            registerButton.classList.add("opacity-50", "cursor-not-allowed");
        }
    </script>
`)
