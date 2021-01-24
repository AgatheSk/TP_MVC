package galerie.controller;

import galerie.dao.ArtisteRepository;
import galerie.dao.TableauRepository;
import galerie.entity.Artiste;
import galerie.entity.Tableau;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author agath
 */
@Controller
@RequestMapping(path = "/tableau")
public class TableauController {
    @Autowired
    private TableauRepository dao;
    @Autowired
    private ArtisteRepository aDao;

    /**
     * Affiche tous les tableaux dans la base
     *
     * @param model pour transmettre les informations à la vue
     * @return le nom de la vue à afficher ('afficheTableaux.html')
     */
    @GetMapping(path = "show")
    public String afficheTousLesTableaux(Model model) {
        model.addAttribute("tableaux", dao.findAll());
        return "afficheTableaux";
    }

    /**
     * Montre le formulaire permettant d'ajouter un tableau
     *
     * @param tableau initialisé par Spring, valeurs par défaut à afficher dans le formulaire
     * @param model
     * @return le nom de la vue à afficher ('formulaireTableaux.html')
     */
    @GetMapping(path = "add")
    public String montreLeFormulairePourAjout(@ModelAttribute("tableau") Tableau tableau, Model model) {
        model.addAttribute("artistes", aDao.findAll());
        return "formulaireTableau";
    }

    /**
     * Appelé par 'formulaireTableau.html', méthode POST
     *
     * @param tableau Un tableau initialisé avec les valeurs saisies dans le formulaire
     * @param redirectInfo pour transmettre des paramètres lors de la redirection
     * @return une redirection vers l'affichage de la liste des tableaux
     */
    @PostMapping(path = "save")
    public String ajouteLaGaleriePuisMontreLaListe(Tableau tableau,
           // Artiste auteur, 
            RedirectAttributes redirectInfo) {
        String message;
        try {
            // cf. https://www.baeldung.com/spring-data-crud-repository-save
            //tableau.setAuteur(auteur);
           // auteur.getOeuvres().add(tableau);
            //aDao.save(auteur);
            dao.save(tableau);
            // Le code du tableau a été initialisé par la BD au moment de l'insertion
            message = "Le tableau '" + tableau.getTitre()+ "' a été correctement enregistré";
        } catch (DataIntegrityViolationException e) {
            // Les noms sont définis comme 'UNIQUE' 
            // En cas de doublon, JPA lève une exception de violation de contrainte d'intégrité
            message = "Erreur : Le tableau '" + tableau.getTitre()+ "' existe déjà";
        }
        // RedirectAttributes permet de transmettre des informations lors d'une redirection,
        // Ici on transmet un message de succès ou d'erreur
        // Ce message est accessible et affiché dans la vue 'afficheTableaux.html'
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show"; // POST-Redirect-GET : on se redirige vers l'affichage de la liste		
    }

    /**
     * Appelé par le lien 'Supprimer' dans 'afficheTableaux.html'
     *
     * @param tableau à partir de l'id du tableau transmis en paramètre, Spring fera une requête SQL SELECT pour
     * chercher le tableau dans la base
     * @param redirectInfo pour transmettre des paramètres lors de la redirection
     * @return une redirection vers l'affichage de la liste des tableaux
     */
    @GetMapping(path = "delete")
    public String supprimeUneCategoriePuisMontreLaListe(@RequestParam("id") Tableau tableau, RedirectAttributes redirectInfo) {
        String message = "Le tableau '" + tableau.getTitre()+ "' a bien été supprimé";
        try {
            dao.delete(tableau); // Ici on peut avoir une erreur (Si il y a une transaction pour ce tableau par exemple)
        } catch (DataIntegrityViolationException e) {
            // violation de contrainte d'intégrité si on essaie de supprimer un tableau qui a une transaction
            message = "Erreur : Impossible de supprimer le tableau '" + tableau.getTitre()+ "', il faut d'abord supprimer son auteur";
        }
        // RedirectAttributes permet de transmettre des informations lors d'une redirection,
        // Ici on transmet un message de succès ou d'erreur
        // Ce message est accessible et affiché dans la vue 'afficheTableaux.html'
        redirectInfo.addFlashAttribute("message", message);
        return "redirect:show"; // on se redirige vers l'affichage de la liste
    }
}
