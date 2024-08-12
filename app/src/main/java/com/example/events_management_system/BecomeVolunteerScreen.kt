package com.example.events_management_system

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.events_management_system.KtorClient.volunteerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeVolunteer(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var Name by remember { mutableStateOf("") }
    var Contact by remember { mutableStateOf("") }
    var ageGroup by remember { mutableStateOf("") }
    val ageGroups = listOf("Under 18", "18-24", "25-34", "35-44", "45-54", "55+")
    var About by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var selecetedAge by remember { mutableStateOf(ageGroups[0]) }

    var registrationTriggered by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    LaunchedEffect(registrationTriggered) {
        if (registrationTriggered) {
            volunteerRegistration(
                Volunteer(
                    email = email,
                    password = password,
                    name = Name,
                    contact = Contact,
                    ageGroup = ageGroup,
                    about = About,
                    terms = agreeTerms
                )
            )
            snackbarHostState.showSnackbar("Congratulations for becoming a volunteer!")
            registrationTriggered = false
            navController.navigate("login")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Email", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start
        )
        TextField(
            value = email,
            onValueChange = {
                email = it
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Password", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            trailingIcon = {
                TextButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Text(if (passwordVisibility) "Hide Password" else "Show Password")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Name", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start
        )
        TextField(
            value = Name,
            onValueChange = { Name = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Contact", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start
        )
        TextField(
            value = Contact,
            onValueChange = { Contact = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Age Group", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = selecetedAge,
                onValueChange = {},
                label = { Text("Select the Age Group") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(),
            ) {
                ageGroups.forEach { label ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(label) },
                        onClick = {
                            selecetedAge = label
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        Text(
            text = "About me and Remarks",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        TextField(
            value = About,
            onValueChange = { About = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            singleLine = false,
            minLines = 3,
            maxLines = 5
        )


        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = agreeTerms, onCheckedChange = {
                agreeTerms = it
            })
            Text(
                text = "I agree to the terms and conditions", modifier = Modifier.padding(8.dp)
            )
        }

        Button(
            onClick = {
                when {
                    email.isBlank() -> snackbarMessage = "Email cannot be empty."
                    !email.contains('@') -> snackbarMessage = "Invalid email format."
                    !agreeTerms -> snackbarMessage = "You must agree to the terms and conditions."
                    else -> registrationTriggered = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                "Register", color = Color.White, fontSize = 18.sp
            )
        }
    }
}

